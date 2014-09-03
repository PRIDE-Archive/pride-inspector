package uk.ac.ebi.pride.toolsuite.gui.component.mzgraph;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenHelpAction;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrievePeptideTask;
import uk.ac.ebi.pride.toolsuite.mzgraph.ChromatogramBrowser;
import uk.ac.ebi.pride.toolsuite.mzgraph.SpectrumBrowser;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;

/**
 * MzGraphViewPane is responsible for visualizing either spectrum or chromatogram
 *
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 14:53:52
 */
public class MzGraphViewPane extends DataAccessControllerPane {

    private static final Logger logger = LoggerFactory.getLogger(MzGraphViewPane.class);
    /**
     * DataAccessController this component belongs to
     */
    private DataAccessController controller;
    /**
     * In memory spectrum browser
     */
    private SpectrumBrowser spectrumBrowser;
    /**
     * In memory chromatogram browser
     */
    private ChromatogramBrowser chromaBrowser;
    /**
     * True indicates it is the first spectrum to be visualized
     */
    private boolean isFirstSpectrum;

    /**
     * True indicates it is the first chromatogram to be visualized
     */
    private boolean isFirstChromatogram;
    /**
     * Reference to Desktop context
     */
    private PrideInspectorContext context;
    /**
     * Subscribe to peptide event
     */
    private SelectPeptideSubscriber peptideSubscriber;

    public MzGraphViewPane(DataAccessController controller) {
        super(controller);

    }

    @Override
    protected void setupMainPane() {
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();

        // setup the main pane
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected void addComponents() {
//        try {
//            if (controller.hasSpectrum()) {
//                createSpectrumBrowser();
//            }
//
//            if (controller.hasChromatogram()) {
//                createChromatogramBrowser();
//            }
//        } catch (DataAccessException ex) {
//            logger.error("Failed to read from data access controller");
//        }
//
//        isFirstSpectrum = true;
//        isFirstChromatogram = true;
    }

    public SpectrumBrowser getSpectrumBrowser() {
        return spectrumBrowser;
    }

    public ChromatogramBrowser getChromaBrowser() {
        return chromaBrowser;
    }

    /**
     * Create spectrum browser to display spectrum
     */
    private void createSpectrumBrowser() {
        spectrumBrowser = new SpectrumBrowser();

        // meta data pane
        MzGraphPropertyPane propPane = new MzGraphPropertyPane();
        propPane.setPreferredSize(new Dimension(200, 200));
        this.addPropertyChangeListener(propPane);

        // add spectrum metadata display pane
        Icon propertyIcon = GUIUtilities.loadIcon(context.getProperty("property.small.icon"));
        String propertyDesc = context.getProperty("property.title");
        String propertyTooltip = context.getProperty("property.tooltip");
        spectrumBrowser.getSidePane().addComponent(propertyIcon, null, propertyTooltip, propertyDesc, propPane);

        // add spectrum help pane
        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
        String helpTooltip = context.getProperty("help.tooltip");
        PrideAction helpAction = new OpenHelpAction(null, helpIcon, helpTooltip);
        helpAction.putValue(Action.SHORT_DESCRIPTION, helpTooltip);
        AbstractButton button = (AbstractButton) spectrumBrowser.getSidePane().addAction(helpAction, false);
        CSH.setHelpIDString(button, "help.mzgraph.spectra");
        button.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
    }

    /**
     * Create chromatogram browser to display chromatogram
     */
    private void createChromatogramBrowser() {
        chromaBrowser = new ChromatogramBrowser();

        // add spectrum help pane
        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
        String helpTooltip = context.getProperty("help.tooltip");
        PrideAction helpAction = new OpenHelpAction(null, helpIcon,helpTooltip);
        helpAction.putValue(Action.SHORT_DESCRIPTION, helpTooltip);
        AbstractButton button = (AbstractButton) chromaBrowser.getSidePane().addAction(helpAction, false);
        CSH.setHelpIDString(button, "help.mzgraph.chroma");
        button.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
    }

//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        if (DataAccessController.MZGRAPH_TYPE.equals(evt.getPropertyName())) {
//            // todo: make it run on EDT
//            Object oldVal = evt.getOldValue();
//            Object newVal = evt.getNewValue();
//            MzGraph mzGraph = (MzGraph) evt.getNewValue();
//            this.removeAll();
//            if (mzGraph instanceof Spectrum) {
//                Spectrum spectrum = (Spectrum) mzGraph;
//                BinaryDataArray mzBinary = spectrum.getMzBinaryDataArray();
//                BinaryDataArray intentBinary = spectrum.getIntensityBinaryDataArray();
//                if (mzBinary != null && intentBinary != null) {
//                    spectrumBrowser.setPeaks(mzBinary.getDoubleArray(), intentBinary.getDoubleArray());
//                    // set source name
//                    if (controller.getType().equals(DataAccessController.Type.XML_FILE)) {
//                        spectrumBrowser.setSource(((File) controller.getSource()).getName());
//                    } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
//                        spectrumBrowser.setSource("Pride Experiment " + controller.getForegroundExperimentAcc());
//                    }
//                    // set id
//                    spectrumBrowser.setId(spectrum.getId());
//                    Peptide peptide = spectrum.getPeptide();
//                    if (peptide != null) {
//                        int peptideLength = peptide.getSequenceLength();
//                        Map<Integer, java.util.List<PTModification>> modifications = AnnotationUtils.createModificationMap(peptide.getModifications(), peptideLength);
//                        spectrumBrowser.setAminoAcidAnnotationParameters(peptide.getSequenceLength(), modifications);
//                        java.util.List<IonAnnotation> ions = AnnotationUtils.convertToIonAnnotations(peptide.getFragmentIons());
//                        spectrumBrowser.addFragmentIons(ions);
//                        if (!ions.isEmpty()) {
//                            spectrumBrowser.enableAnnotationControl(true);
//                            if (isFirstSpectrum) {
//                                spectrumBrowser.setAnnotationControlVisible(true);
//                            }
//                        }
//                    } else {
//                        if (isFirstSpectrum) {
//                            SideToolBarPanel sidePane = spectrumBrowser.getSidePane();
//                            String actionCmd = context.getProperty("property.title");
//                            if (!sidePane.isToggled(actionCmd)) {
//                                sidePane.invokeAction(actionCmd);
//                            }
//                        }
//                    }
//                    isFirstSpectrum = false;
//                    this.add(spectrumBrowser, BorderLayout.CENTER);
//                }
//            } else if (mzGraph instanceof Chromatogram) {
//                Chromatogram chroma = (Chromatogram) mzGraph;
//                chromaBrowser.setGraphData(chroma.getTimeArray().getDoubleArray(), chroma.getIntensityArray().getDoubleArray());
//                // set source name
//                if (controller.getType().equals(DataAccessController.Type.XML_FILE)) {
//                    chromaBrowser.setSource(((File) controller.getSource()).getName());
//                } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
//                    chromaBrowser.setSource("Pride Experiment " + controller.getForegroundExperimentAcc());
//                }
//                // set id
//                chromaBrowser.setId(chroma.getId());
//                isFirstChromatogram = false;
//                this.add(chromaBrowser, BorderLayout.CENTER);
//            }
//            this.revalidate();
//            this.repaint();
//            this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, oldVal, newVal);
//        }
//    }

    public void subscribeToEventBus() {
        // get local event bus
        EventService eventBus = ContainerEventServiceFinder.getEventService(this);

        // subscriber
        peptideSubscriber = new SelectPeptideSubscriber();

        // subscribeToEventBus
        eventBus.subscribe(PSMEvent.class, peptideSubscriber);
    }


    private class SelectPeptideSubscriber implements EventSubscriber<PSMEvent> {

        @Override
        public void onEvent(PSMEvent event) {
            Comparable peptideId = event.getPeptideId();
            Comparable protId = event.getIdentificationId();

            Task newTask = new RetrievePeptideTask(controller, protId, peptideId);
            TaskUtil.startBackgroundTask(newTask, controller);
        }
    }
}

