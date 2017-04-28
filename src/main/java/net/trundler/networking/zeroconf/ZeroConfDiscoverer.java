package net.trundler.networking.zeroconf;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import net.trundler.networking.zeroconf.listeners.MyServiceListener;
import net.trundler.networking.zeroconf.listeners.MyServiceTypeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class ZeroConfDiscoverer {

    private static final Logger logger = LogManager.getRootLogger();

    private static final JsonFactory factory = new JsonFactory();

    private static final int shutdownWait = 15; // secs

    /**
     * Write UPNP event data to file.
     *
     * @param event Zeroconf event
     */
    private static void writeReportToFile(final ServiceEvent event) {

        final String key = event.getInfo().getKey();
        logger.info("Write file for key: {}", key);


        try (OutputStream os = Files.newOutputStream(Paths.get("zeroconf_" + key + ".json"))) {

            ZeroConfDiscoverer.serialize(event, os);

        } catch (final IOException ex) {
            logger.error(ex);
        }
    }


    /**
     * Serialize UPNP device data to JSON.
     *
     * @param event Zeroconf event
     * @param os    Output stream
     * @throws IOException Problem during serialisation to JSON
     */
    private static void serialize(final ServiceEvent event, final OutputStream os) throws IOException {
        try (JsonGenerator generator = factory.createGenerator(os).useDefaultPrettyPrinter()) {

            generator.writeStartObject();
            generator.writeStringField("name", event.getName());
            generator.writeStringField("type", event.getType());

            final ServiceInfo info = event.getInfo();

            generator.writeStringField("application", info.getApplication());
            generator.writeStringField("domain", info.getDomain());
            generator.writeStringField("key", info.getKey());
            generator.writeStringField("protocol", info.getProtocol());
            generator.writeStringField("name", info.getName());
            generator.writeNumberField("port", info.getPort());
            generator.writeNumberField("priority", info.getPriority());
            generator.writeStringField("qn", info.getQualifiedName());

            final String[] urls = info.getURLs();
            if (urls.length > 0) {
                generator.writeArrayFieldStart("urls");

                for (final String url : urls) {
                    generator.writeString(url);
                }

                generator.writeEndArray();
            }


            final String[] hostAddresses = info.getHostAddresses();
            if (hostAddresses.length > 0) {
                generator.writeArrayFieldStart("hostAddresses");

                for (final String address : hostAddresses) {
                    generator.writeString(address);
                }


                generator.writeEndArray();

            }
            generator.writeEndObject();

            os.flush();
        }

    }


    public static void main(final String[] args) throws InterruptedException {
        try {
            // Create a JmDNS instance
            final JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Create service listener
            final MyServiceListener serviceListener = new MyServiceListener();

            // Create service type listener
            final MyServiceTypeListener stListener = new MyServiceTypeListener(jmdns, serviceListener);

            // Register
            jmdns.addServiceTypeListener(stListener);

            // Wait a bit
            logger.info("Waiting {} seconds...", shutdownWait);
            Thread.sleep(TimeUnit.SECONDS.toMillis(shutdownWait));


            for (final ServiceEvent serviceEvent : serviceListener.getEvents()) {
                ZeroConfDiscoverer.writeReportToFile(serviceEvent);
            }

            // Cleanup
            jmdns.removeServiceTypeListener(stListener);
            jmdns.unregisterAllServices();

        } catch (final IOException e) {
            logger.error(e);
        }

        System.exit(0);
    }
}
