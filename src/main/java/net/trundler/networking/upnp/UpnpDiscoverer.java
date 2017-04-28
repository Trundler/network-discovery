package net.trundler.networking.upnp;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import net.trundler.networking.upnp.listeners.MyRegistryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.RegistryListener;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Runs a simple UPnP discovery procedure.
 */
public class UpnpDiscoverer {

    private static final Logger logger = LogManager.getRootLogger();

    private static final JsonFactory factory = new JsonFactory();

    private static final int shutdownWait = 15; // secs

    /**
     * Write device data to file
     *
     * @param device UPnP device
     */
    private static void writeReportToFile(final Device device) {

        final UDN udn = device.getIdentity().getUdn();
        logger.info("Write file for UDN: {}", udn.getIdentifierString());

        try (OutputStream os = Files.newOutputStream(Paths.get("upnp_" + udn.getIdentifierString() + ".json"))) {

            UpnpDiscoverer.serialize(device, os);

        } catch (final IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * Serialize UPNP device data to JSON.
     *
     * @param device UPnP device
     * @param os     Output stream
     * @throws IOException Problem during serialisation to JSON
     */
    private static void serialize(final Device device, final OutputStream os) throws IOException {
        try (JsonGenerator generator = factory.createGenerator(os).useDefaultPrettyPrinter()) {

            generator.writeStartObject();
            generator.writeStringField("id", device.getIdentity().getUdn().getIdentifierString());
            generator.writeStringField("manufacturer", device.getDetails().getManufacturerDetails().getManufacturer());
            generator.writeStringField("model", device.getDetails().getModelDetails().getModelName());
            generator.writeStringField("modelNumber", device.getDetails().getModelDetails().getModelNumber());
            generator.writeStringField("modelDescription", device.getDetails().getModelDetails().getModelDescription());
            generator.writeStringField("type", device.getType().getType());

            if (device instanceof RemoteDevice) {
                generator.writeStringField("descriptorURL", String.valueOf(((RemoteDevice) device).getIdentity().getDescriptorURL()));
            }

            final ServiceType[] serviceTypes = device.findServiceTypes();
            if (serviceTypes.length > 0) {
                generator.writeArrayFieldStart("ServiceTypes");

                for (final ServiceType st : serviceTypes) {
                    generator.writeString(st.toFriendlyString());
                }

                generator.writeEndArray();
            }

//            final Device[] embeddedDevices = device.;
//            Arrays.stream(embeddedDevices).forEach(d -> logger.info("##{}",d.getDisplayString()));

        }

    }

    public static void main(final String[] args) throws Exception {

        // Callback to receive UPnP discovery information
        final RegistryListener listener = new MyRegistryListener();

        // Register listener
        final UpnpService upnpService = new UpnpServiceImpl(listener);

        // Discover devices
        upnpService.getControlPoint().search(new STAllHeader());

        // Wait for some time
        logger.info("Waiting {} seconds...", shutdownWait);
        Thread.sleep(TimeUnit.SECONDS.toMillis(shutdownWait));

        // Report devices
        upnpService.getRegistry().getDevices().forEach(UpnpDiscoverer::writeReportToFile);

        // Cleanup
        upnpService.shutdown();
    }
}