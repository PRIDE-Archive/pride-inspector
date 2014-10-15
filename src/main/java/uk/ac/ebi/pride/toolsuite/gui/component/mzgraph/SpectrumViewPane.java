package uk.ac.ebi.pride.toolsuite.gui.component.mzgraph;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideDBAccessControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.*;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.SideToolBarPanel;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenHelpAction;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.SpectrumEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.subscriber.PeptideSpectrumEventSubscriber;
import uk.ac.ebi.pride.toolsuite.gui.event.subscriber.SpectrumEventSubscriber;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.utils.AnnotationUtils;
import uk.ac.ebi.pride.toolsuite.gui.utils.PeptideTranslate;
import uk.ac.ebi.pride.utilities.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.utilities.iongen.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.utilities.mol.MoleculeUtilities;
import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.toolsuite.mzgraph.SpectrumBrowser;
import uk.ac.ebi.pride.toolsuite.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.ExperimentalFragmentedIonsTable;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalFragmentedIonsTableModel;
import uk.ac.ebi.pride.toolsuite.mzgraph.gui.data.ExperimentalParams;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Panel to display spectrum
 * <p/>
 * User: rwang
 * Date: 10/06/11
 * Time: 14:42
 */
public class SpectrumViewPane extends DataAccessControllerPane<Spectrum, Void> implements EventBusSubscribable {

    private static final Logger logger = LoggerFactory.getLogger(SpectrumViewPane.class);

    /**
     * In memory spectrum browser
     */
    private SpectrumBrowser spectrumBrowser;
    /**
     * True indicates it is the first spectrum to be visualized
     */
    private boolean isFirstSpectrum;
    /**
     * Whether to show the side panel by default
     */
    private boolean showSidePanel;
    /**
     * Indicates whether the data source contains spectra
     */
    private boolean spectrumAvailable;
    /**
     * message to show when there is no spectra
     */
    private String spectrumUnavailableMessage;
    /**
     * Subscribe to peptide event
     */
    private PeptideSpectrumEventSubscriber spectrumSubscriber;

    private SpectrumEventSubscriber spectrumSelectSubscriber;

    public SpectrumViewPane(DataAccessController controller, boolean showSidePanel) {
        super(controller);
        this.showSidePanel = showSidePanel;
        try {
            this.spectrumAvailable = controller.hasSpectrum();
        } catch (DataAccessException e) {
            logger.error("Failed to check the spectrum availability", e);
        }
        this.spectrumUnavailableMessage = appContext.getProperty("no.spectrum.warning.message");
    }

    @Override
    protected void setupMainPane() {
        // setup the main pane
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected void addComponents() {
        isFirstSpectrum = true;
        spectrumBrowser = new SpectrumBrowser();

        // meta data pane
        MzGraphPropertyPane propPane = new MzGraphPropertyPane();
        propPane.setPreferredSize(new Dimension(200, 200));
        this.addPropertyChangeListener(propPane);

        // add spectrum metadata display pane
        Icon propertyIcon = GUIUtilities.loadIcon(appContext.getProperty("property.small.icon"));
        String propertyDesc = appContext.getProperty("property.title");
        String propertyTooltip = appContext.getProperty("property.tooltip");
        spectrumBrowser.getSidePane().addComponent(propertyIcon, null, propertyTooltip, propertyDesc, propPane);

        // add spectrum help pane
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        String helpTooltip = appContext.getProperty("help.tooltip");
        PrideAction helpAction = new OpenHelpAction(null, helpIcon, "help.mzgraph.spectra");
        helpAction.putValue(Action.SHORT_DESCRIPTION, helpTooltip);
        spectrumBrowser.getSidePane().addAction(helpAction, false);

        this.add(spectrumBrowser, BorderLayout.CENTER);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (!spectrumAvailable) {
            // paint a semi transparent glass pane
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // get bound
            Rectangle clip = g.getClipBounds();

            // set composite
            Composite oldComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.SrcOver.derive(0.30f));
            // set colour
            g2.setPaint(new Color(185, 218, 201));
            // paint panel
            g2.fillRect(clip.x, clip.y, clip.width, clip.height);

            // reset composite
            g2.setComposite(oldComposite);

            // paint message
            g2.setPaint(Color.gray);
            g2.setFont(g2.getFont().deriveFont(20f).deriveFont(Font.BOLD));
            FontMetrics fontMetrics = g2.getFontMetrics();
            int msgWidth = fontMetrics.stringWidth(spectrumUnavailableMessage);
            int xPos = clip.x + clip.width / 2 - msgWidth / 2;
            g2.drawString(spectrumUnavailableMessage, xPos, clip.height / 2);
            g2.dispose();
        }
    }

    /**
     * Subscribe to local event bus
     */
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        spectrumSubscriber = new PeptideSpectrumEventSubscriber(controller, this);
        spectrumSelectSubscriber = new SpectrumEventSubscriber(controller, this);

        // subscribeToEventBus
        eventBus.subscribe(PSMEvent.class, spectrumSubscriber);
        eventBus.subscribe(SpectrumEvent.class, spectrumSelectSubscriber);
    }

    private int getCharge(Peptide peptide) {
        int chartState = peptide.getSpectrumIdentification().getChargeState();
        return chartState;
    }

    /**
     * this is a threshold based on delta m/z value. if the spectrum delta m/z value
     * great than this, means maybe there exists some errors in the experimental,
     * system not show any annotations (including auto and manual) in table panel.
     */
    private boolean isOverflow(Peptide peptide) {
        boolean overflow = false;
        try {
            String sequence = peptide.getSequence();
            Comparable specId = peptide.getSpectrum().getId();

            int charge = getCharge(peptide);
            if (charge <= 0) {
                return true;
            }

            double mz = controller.getSpectrumPrecursorMz(specId);

            java.util.List<Modification> mods = peptide.getModifications();

            java.util.List<Double> ptmMasses = new ArrayList<Double>();
            for (Modification mod : mods) {
                java.util.List<Double> monoMasses = mod.getMonoisotopicMassDelta();
                if (monoMasses != null && !monoMasses.isEmpty()) {
                    ptmMasses.add(monoMasses.get(0));
                }
            }
            Double deltaMass = MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);

            if (deltaMass == null || (deltaMass != null && Double.compare(Math.abs(deltaMass.doubleValue()), FragmentationTablePane.DELTA_MZ_THRESHOLD) >= 0)) {
                overflow = true;
            }
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
        }

        return overflow;
    }

    @Override
    public void succeed(TaskEvent<Spectrum> spectrumTaskEvent) {
        Spectrum spectrum = spectrumTaskEvent.getValue();

        BinaryDataArray mzBinary = spectrum == null ? null : spectrum.getMzBinaryDataArray();
        BinaryDataArray intentBinary = spectrum == null ? null : spectrum.getIntensityBinaryDataArray();

        if (spectrum != null && mzBinary != null && intentBinary != null && !mzBinary.isEmpty() && !intentBinary.isEmpty()) {
            spectrumBrowser.setPeaks(mzBinary.getDoubleArray(), intentBinary.getDoubleArray());
            // set source name
            if (controller.getType().equals(DataAccessController.Type.XML_FILE)) {
                spectrumBrowser.setSource(((File) controller.getSource()).getName());
            } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
                spectrumBrowser.setSource("Pride Experiment " + ((PrideDBAccessControllerImpl) controller).getExperimentAcc());
            }
            // set id
            spectrumBrowser.setId(spectrum.getId());
            spectrumBrowser.clearMassDifferences();
            spectrumBrowser.setAnnotationControlVisible(true);
            Peptide peptide = spectrum.getPeptide();
            if (peptide != null) {
                int peptideLength = peptide.getSequenceLength();
                Map<Integer, java.util.List<PTModification>> modifications = AnnotationUtils.createModificationMap(peptide.getModifications(), peptideLength);
                spectrumBrowser.setAminoAcidAnnotationParameters(peptide.getSequenceLength(), modifications);
                java.util.List<IonAnnotation> ions = AnnotationUtils.convertToIonAnnotations(peptide.getFragmentation());

                if (!ions.isEmpty() && !peptide.isFragmentIonsChargeAnnotated()) {
                    // manual annotations
                    spectrumBrowser.addFragmentIons(ions);
                } else if (spectrumBrowser.getSpectrumPanel().getModel().getIonDataset().getSeriesCount() == 0) {
                    // no manual annotations, but propertyChange() method has executed before succeed() method,
                    // that means auto annotations generated by propertyChange() method will be overwrite.
                    // Thus, at this time, we will create a temporary tableModel to generate auto annotations.
                    if (isOverflow(peptide)) {
                        // delta m/z too high, not generate auto annotations.
                        spectrumBrowser.getSpectrumPanel().addDeltaOverflowAnnotation();
                    } else {
                        spectrumBrowser.getSpectrumPanel().removeDeltaOverflowAnnotation();

                        uk.ac.ebi.pride.utilities.mol.Peptide myPeptide = new PeptideTranslate(peptide).translate();
                        int charge = getCharge(peptide);
                        charge = charge > 2 ? 2 : charge;
                        PrecursorIon precursorIon = new DefaultPrecursorIon(myPeptide, charge);
                        ExperimentalParams params = ExperimentalParams.getInstance();
                        ExperimentalFragmentedIonsTableModel myTableModel = new ExperimentalFragmentedIonsTableModel(precursorIon, params.getIonPair());
                        myTableModel.setPeaks(mzBinary.getDoubleArray(), intentBinary.getDoubleArray());

                        myTableModel.setCalculate(true);
                        spectrumBrowser.addFragmentIons(myTableModel.getAutoAnnotations());
                    }
                }

                if (showSidePanel && !ions.isEmpty()) {
                    spectrumBrowser.enableAnnotationControl(true);
                    if (isFirstSpectrum) {
                        spectrumBrowser.setAnnotationControlVisible(true);
                    }
                }
            } else {
                if (isFirstSpectrum && showSidePanel) {
                    SideToolBarPanel sidePane = spectrumBrowser.getSidePane();
                    String actionCmd = appContext.getProperty("property.title");
                    if (!sidePane.isToggled(actionCmd)) {
                        sidePane.invokeAction(actionCmd);
                    }
                }
            }
            isFirstSpectrum = false;
        }
        this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, "", spectrum);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);

        if (evt.getPropertyName().equals(ExperimentalFragmentedIonsTable.FLUSH_TABLEMODEL)) {
            ExperimentalFragmentedIonsTableModel tableModel = (ExperimentalFragmentedIonsTableModel) evt.getNewValue();

            spectrumBrowser.getSpectrumPanel().removeIonAnnotations();
            spectrumBrowser.getSpectrumPanel().removeDeltaOverflowAnnotation();

            if (!tableModel.isShowAuto()) {
                spectrumBrowser.addFragmentIons(tableModel.getAllManualAnnotations());
            } else if (tableModel.isCalculate()) {
                java.util.List<IonAnnotation> ionAnnotations = tableModel.getAutoAnnotations();
                spectrumBrowser.addFragmentIons(ionAnnotations);
            } else {
                // delta m/z too high, not generate auto annotations.
                spectrumBrowser.getSpectrumPanel().addDeltaOverflowAnnotation();
            }

            spectrumBrowser.revalidate();
            spectrumBrowser.repaint();
        }
    }


}
