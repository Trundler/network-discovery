package net.trundler.networking.zeroconf.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

/**
 * Created by wessels on 24/4/17.
 */
public class MyServiceTypeListener implements ServiceTypeListener {

    private static final Logger logger = LogManager.getRootLogger();


    final JmDNS jmdns;
    final ServiceListener serviceListener;

    public MyServiceTypeListener(final JmDNS jmdns, final ServiceListener serviceListener) {
        this.jmdns = jmdns;
        this.serviceListener = serviceListener;
    }


    /**
     * A new service type was discovered.
     *
     * @param event The service event providing the fully qualified type of the service.
     */
    @Override
    public void serviceTypeAdded(final ServiceEvent event) {
        logger.debug("Type added: {}", event.getType());
        jmdns.addServiceListener(event.getType(), serviceListener);
    }

    /**
     * A new subtype for the service type was discovered.
     * <p>
     * <pre>
     * &lt;sub&gt;._sub.&lt;app&gt;.&lt;protocol&gt;.&lt;servicedomain&gt;.&lt;parentdomain&gt;.
     * </pre>
     *
     * @param event The service event providing the fully qualified type of the service with subtype.
     * @since 3.2.0
     */
    @Override
    public void subTypeForServiceTypeAdded(final ServiceEvent event) {

        logger.debug("Subtype added: {}", event.getType());
        jmdns.addServiceListener(event.getType(), serviceListener);
    }
}
