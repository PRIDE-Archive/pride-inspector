package uk.ac.ebi.pride.toolsuite.gui.component.peptide;

import org.bushe.swing.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.ExtraProteinDetailAction;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.mzgraph.FragmentationTablePane;
import uk.ac.ebi.pride.toolsuite.gui.component.mzgraph.SpectrumViewPane;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.ProteinSequencePane;

import javax.swing.*;
import java.awt.*;

/**
 * Visualize both spectrum and protein sequence
 * <p/>
 * User: rwang
 * Date: 10/06/11
 * Time: 16:43
 */
public class PeptideVizPane extends DataAccessControllerPane implements EventBusSubscribable {

    private static Logger logger = LoggerFactory.getLogger(PeptideVizPane.class);
    /**
     * the default background color
     */
    private static final Color BACKGROUND_COLOUR = Color.white;

    private SpectrumViewPane spectrumViewPane;

    private ProteinSequencePane proteinSequencePane;

    private FragmentationTablePane fragmentationTablePane;

    private int proteinSequencePaneIndex = 0;

    JTabbedPane tabbedPane;

    public PeptideVizPane(DataAccessController controller, JComponent parentComponent) {
        super(controller, parentComponent);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    @Override
    protected void addComponents() {
        // tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BACKGROUND_COLOUR);

        // tab index
        int tabIndex = 0;

        try {
            if (controller.hasSpectrum()) {
                addSpectrumViewPane(tabIndex);
                tabIndex++;

                //Fragmentation Table Panel
                fragmentationTablePane = new FragmentationTablePane(controller);
                tabbedPane.insertTab(appContext.getProperty("fragment.tab.title"), null,
                        fragmentationTablePane, appContext.getProperty("fragment.tab.tooltip"), tabIndex);
                tabIndex++;

                fragmentationTablePane.getMzTablePanel().addPropertyChangeListener(spectrumViewPane);
            }
        } catch (DataAccessException e) {
            String msg = "Failed to check the availability of spectrum";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }

        // protein sequence pane
        Action action = null;
        if (parentComponent != null && parentComponent instanceof PeptideTabPane) {
            action = new ExtraProteinDetailAction(controller);
        }

        proteinSequencePane = new ProteinSequencePane(controller, action);
        JScrollPane scrollPane = new JScrollPane(proteinSequencePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOUR);
        tabbedPane.insertTab(appContext.getProperty("protein.sequence.tab.title"), null,
                scrollPane, appContext.getProperty("protein.sequence.tab.tooltip"), tabIndex);
        proteinSequencePaneIndex = tabIndex;

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {

        if (spectrumViewPane != null) {
            spectrumViewPane.subscribeToEventBus(null);
            fragmentationTablePane.subscribeToEventBus(null);
        }
        proteinSequencePane.subscribeToEventBus(null);
    }

    public void addSpectrumViewPane(int tabIndex) {
        // Spectrum view pane
        spectrumViewPane = new SpectrumViewPane(controller, true);
        tabbedPane.insertTab(appContext.getProperty("spectrum.tab.title"), null,
                spectrumViewPane, appContext.getProperty("spectrum.tab.tooltip"), tabIndex);
    }

    public void addSpectrumViewPane() {
        // Spectrum view pane
        spectrumViewPane = new SpectrumViewPane(controller, true);

        int tabbedPaneIndex = 0;
        tabbedPane.removeTabAt(proteinSequencePaneIndex);

        tabbedPane.insertTab(appContext.getProperty("spectrum.tab.title"), null,
                spectrumViewPane, appContext.getProperty("spectrum.tab.tooltip"), tabbedPaneIndex);
        tabbedPaneIndex++;

        JScrollPane scrollPane = new JScrollPane(proteinSequencePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOUR);
        tabbedPane.insertTab(appContext.getProperty("protein.sequence.tab.title"), null,
                scrollPane, appContext.getProperty("protein.sequence.tab.tooltip"), tabbedPaneIndex);
        proteinSequencePaneIndex = tabbedPaneIndex;

        spectrumViewPane.subscribeToEventBus(null);
    }

    public void addFragmentationViewPane() {

        fragmentationTablePane = new FragmentationTablePane(controller);

        int tabbedPaneIndex = 1;
        tabbedPane.removeTabAt(proteinSequencePaneIndex);

        tabbedPane.insertTab(appContext.getProperty("fragment.tab.title"), null,
                fragmentationTablePane, appContext.getProperty("fragment.tab.tooltip"), tabbedPaneIndex);
        tabbedPaneIndex++;

        JScrollPane scrollPane = new JScrollPane(proteinSequencePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOUR);
        tabbedPane.insertTab(appContext.getProperty("protein.sequence.tab.title"), null,
                scrollPane, appContext.getProperty("protein.sequence.tab.tooltip"), tabbedPaneIndex);
        proteinSequencePaneIndex = tabbedPaneIndex;

        fragmentationTablePane.subscribeToEventBus(null);
    }

    public void removeSpectrumViewPane() {
        if (spectrumViewPane != null) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getComponent(i) instanceof SpectrumViewPane) {
                    tabbedPane.remove(i);
                    spectrumViewPane = null;
                    proteinSequencePaneIndex--;
                }
            }
        }
    }

    public void removeFragmentationViewPane() {
        if (fragmentationTablePane != null) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getComponent(i) instanceof FragmentationTablePane) {
                    tabbedPane.remove(i);
                    fragmentationTablePane = null;
                    proteinSequencePaneIndex--;
                }
            }
        }
    }


}
