package net.trundler.networking.upnp.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

/**
 * Created by wessels on 28/4/17.
 */
public class MyRegistryListener implements RegistryListener {

    private static final Logger logger = LogManager.getRootLogger();

    public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice device) {
        logger.debug("Remove device discovery started: {}", device.getDisplayString());
    }

    public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice device, final Exception ex) {
        logger.error("Remote device discovery failed: {}", device.getDisplayString(), ex);
    }

    public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
        logger.debug("Remote device added: {}", device.getDisplayString());
    }

    public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
        logger.debug("Remote device updated: {}", device.getDisplayString());
    }

    public void remoteDeviceRemoved(final Registry registry, final RemoteDevice device) {
        logger.debug("Remote device removed: {}", device.getDisplayString());
    }

    public void localDeviceAdded(final Registry registry, final LocalDevice device) {
        logger.debug("Local device added: {}", device.getDisplayString());
    }

    public void localDeviceRemoved(final Registry registry, final LocalDevice device) {
        logger.debug("Local device removed: {}", device.getDisplayString());
    }

    public void beforeShutdown(final Registry registry) {
        logger.debug("Before shutdown, the registry has devices: {}", registry.getDevices().size());

    }

    public void afterShutdown() {
        logger.debug("Shutdown of registry complete!");
    }
}
