package net.trundler.networking.zeroconf.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wessels on 24/4/17.
 */
public class MyServiceListener implements ServiceListener {

    private static final Logger logger = LogManager.getRootLogger();
    private final List<ServiceEvent> events = new ArrayList<>();

    public List<ServiceEvent> getEvents() {
        return events;
    }

    @Override
    public void serviceAdded(final ServiceEvent event) {
        logger.debug("Service added: type={} name={}", event.getType(), event.getName());

    }

    @Override
    public void serviceRemoved(final ServiceEvent event) {
        logger.debug("Service removed: type={} name={}", event.getType(), event.getName());
    }

    @Override
    public void serviceResolved(final ServiceEvent event) {
        logger.debug("Service removed: type={} name={}", event.getType(), event.getName());
        events.add(event);
    }

}


