package uk.ac.ebi.pride.toolsuite.gui.component;

import org.bushe.swing.event.EventService;

/**
 * Implement this interface to subscribe to local event bus
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 15:16
 */
public interface EventBusSubscribable {

    /**
     * Subscribe to event bus
     * @param eventBus  event buses to subscribe to
     */
    void subscribeToEventBus(EventService eventBus);
}
