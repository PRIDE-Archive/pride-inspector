package uk.ac.ebi.pride.toolsuite.gui.component.mzdata;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.mzgraph.ChromatogramViewPane;
import uk.ac.ebi.pride.toolsuite.gui.component.mzgraph.SpectrumViewPane;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ChromatogramEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.SpectrumEvent;

import javax.swing.*;
import java.awt.*;

/**
 * This panel is used for visualizing either spectrum or chromatogram
 * <p/>
 * User: rwang
 * Date: 13/06/11
 * Time: 10:12
 */
public class MzDataVizPane extends DataAccessControllerPane implements EventBusSubscribable {
    private static Logger logger = LoggerFactory.getLogger(MzDataVizPane.class);

    private static final Color BACKGROUND_COLOUR = Color.white;

    private SpectrumViewPane spectrumViewPane;
    private ChromatogramViewPane chromaViewPane;
    private SpectrumEventSubscriber spectrumEventSubscriber;
    private ChromatogramEventSubscriber chromaEventSubscriber;

    public MzDataVizPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBackground(BACKGROUND_COLOUR);
    }

    @Override
    protected void addComponents() {

        try {
            if (controller.hasSpectrum()) {
                // Spectrum view pane
                spectrumViewPane = new SpectrumViewPane(controller, true);
            }
        } catch (DataAccessException e) {
            String msg = "Failed to check the availability of spectrum";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }

        try {
            if (controller.hasChromatogram()) {
                // chromatogram pane
                chromaViewPane = new ChromatogramViewPane(controller);
            }
        } catch (DataAccessException e) {
            String msg = "Failed to check the availability of chromatogram";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));

        }

        this.add(spectrumViewPane == null ? (chromaViewPane == null ? new JPanel() : chromaViewPane) : spectrumViewPane, BorderLayout.CENTER);
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        if (spectrumViewPane != null) {
            spectrumViewPane.subscribeToEventBus(eventBus);
        }

        if (chromaViewPane != null) {
            chromaViewPane.subscribeToEventBus(eventBus);
        }

        // spectrum subscribe
        spectrumEventSubscriber = new SpectrumEventSubscriber();
        eventBus.subscribe(SpectrumEvent.class, spectrumEventSubscriber);

        // chromatogram subscribe
        chromaEventSubscriber = new ChromatogramEventSubscriber();
        eventBus.subscribe(ChromatogramEvent.class, chromaEventSubscriber);
    }


    private class SpectrumEventSubscriber implements EventSubscriber<SpectrumEvent> {

        @Override
        public void onEvent(SpectrumEvent event) {
            MzDataVizPane.this.removeAll();
            MzDataVizPane.this.add(spectrumViewPane);
            MzDataVizPane.this.revalidate();
            MzDataVizPane.this.repaint();
        }
    }

    private class ChromatogramEventSubscriber implements EventSubscriber<ChromatogramEvent> {

        @Override
        public void onEvent(ChromatogramEvent event) {
            MzDataVizPane.this.removeAll();
            MzDataVizPane.this.add(chromaViewPane);
            MzDataVizPane.this.revalidate();
            MzDataVizPane.this.repaint();
        }
    }
}
