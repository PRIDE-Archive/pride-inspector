package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.UserParam;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.access.GeneralMetaDataGroup;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.report.SummaryReportMessage;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.toolsuite.gui.event.SummaryReportEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveMetaDataTask;
import uk.ac.ebi.pride.utilities.term.CvTermReference;
import uk.ac.ebi.pride.util.NumberUtilities;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

/**
 * MetaDataTabPane displays all the meta data shared across the data source/experiment.
 * It listens to the following property change event:
 * <p/>
 * User: rwang
 * Date: 05-Mar-2010
 * Time: 15:12:07
 */
public class MetaDataTabPane extends DataAccessControllerPane<GeneralMetaDataGroup, Void> implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(MetaDataTabPane.class);

    private static final String GENERAL = "Experiment General";
    private static final String SAMPLE_PROTOCOL = "Sample & Protocol";
    private static final String INSTRUMENT_SOFTWARE = "Instrument & Processing";
    private static final String IDENTIFICATION_METADATA = "Identification Protocol";

    private static final String PANE_TITLE = "Overview";

    private JPanel metaDataTopPanel;
    private JPanel metaDataContainer;
    private JPanel metaDataControlBar;
    private JPanel generalMetadataPanel;
    private JPanel sampleProtocolMetadataPanel;
    private JPanel instrumentProcMetadataPanel;
    private PrideInspectorContext context;
    private IdentificationMetadataPanel identificationMetadataPanel;


    public MetaDataTabPane(DataAccessController controller, JComponent component) {
        super(controller, component);
    }

    @Override
    protected void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.setTitle(PANE_TITLE);

        // set the final icon
        context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
//        this.setIcon(GUIUtilities.loadIcon(context.getProperty("general.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(context.getProperty("general.tab.loading.icon.small")));
    }

    @Override
    public void populate() {
        // start retrieving meta data
        retrieveMetaData();
    }

    @Override
    public void started(TaskEvent event) {
        parentComponent.revalidate();
        parentComponent.repaint();
    }

    @Override

    public void succeed(TaskEvent<GeneralMetaDataGroup> metaDataTaskEvent) {

        final GeneralMetaDataGroup metaData = metaDataTaskEvent.getValue();

        Runnable run = new Runnable() {

            @Override
            public void run() {
                // init container
                createContainer();

                // create meta data panels
                createMetaDataPanels(metaData);

                // tool bar
                createTopPanel(metaData);

                // add to scroll pane
                JScrollPane scrollPane = new JScrollPane(metaDataContainer,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                MetaDataTabPane.this.add(scrollPane, BorderLayout.CENTER);

                // set vertical scroll bar's speed
                scrollPane.getVerticalScrollBar().setUnitIncrement(100);
            }
        };

        try {
            EDTUtils.invokeAndWait(run);
        } catch (InvocationTargetException e) {
            String msg = "Failed to create meta data panels";
            logger.error(msg, e);

        } catch (InterruptedException e) {
            String msg = "Failed to create meta data panels";
            logger.error(msg, e);
        }

        // notify key information, such as: FDR
        notifyKeyInfo(metaData);
    }

    /**
     * Notify experiment summary for certain data
     * Such as: FDR, Tranche Link
     *
     * @param metaData Experimental metadata
     */
    private void notifyKeyInfo(GeneralMetaDataGroup metaData) {
        java.util.List<CvParam> cvParams = metaData.getMetaData().getCvParams();
        if (!cvParams.isEmpty()) {
            for (CvParam cvParam : cvParams) {
                String acc = cvParam.getAccession();
                String value = cvParam.getValue();
                // FDR
                if (CvTermReference.PEPTIDE_GLOBAL_FDR.getAccession().equals(acc)) {
                    // peptide FDR
                    if (NumberUtilities.isNumber(value)) {
                        double dVal = NumberUtilities.scaleDouble(Double.parseDouble(value), 5);
                        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.INFO, "Peptide FDR: " + dVal, "This data source contains petpide golabl FDR")));
                    }
                } else if (CvTermReference.PROTEIN_GLOBAL_FDR.getAccession().equals(acc)) {
                    // peptide FDR
                    if (NumberUtilities.isNumber(value)) {
                        double dVal = NumberUtilities.scaleDouble(Double.parseDouble(value), 5);
                        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.INFO, "Protein FDR: " + dVal, "This data source contains protein golabl FDR")));
                    }
                }

            }
        }

        java.util.List<UserParam> userParams = metaData.getMetaData().getUserParams();
        if (!userParams.isEmpty()) {
            for (UserParam userParam : userParams) {
                String name = userParam.getName().toLowerCase();
                String value = userParam.getValue();
                // tranche link
                if (name.contains("tranche")) {
                    EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.INFO, "Tranche Link Available", "This data source contains Tranche links")));
                    break;
                }
            }
        }
    }

    // called when a background task finished to reset the icon
    @Override
    public void finished(TaskEvent<Void> event) {
       // showIcon(getIcon());
    }

    /**
     * Show a different icon if the parent component is not null and an instance of DataContentDisplayPane
     *
     * @param icon icon to show
     */
    private void showIcon(Icon icon) {
        if (parentComponent != null && parentComponent instanceof ControllerContentPane) {
            ControllerContentPane contentPane = (ControllerContentPane) parentComponent;
            contentPane.setTabIcon(contentPane.getMetaDataTabIndex(), icon);
        }
    }

    private void retrieveMetaData() {
        RetrieveMetaDataTask retrieveTask = new RetrieveMetaDataTask(controller);

        // register task listener
        retrieveTask.addTaskListener(this);

        TaskUtil.startBackgroundTask(retrieveTask, controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();
        metaDataContainer.removeAll();
        metaDataContainer.add(metaDataTopPanel, BorderLayout.NORTH);
        if (GENERAL.equals(cmd)) {
            metaDataContainer.add(generalMetadataPanel, BorderLayout.CENTER);
        } else if (SAMPLE_PROTOCOL.equals(cmd)) {
            metaDataContainer.add(sampleProtocolMetadataPanel, BorderLayout.CENTER);
        } else if (INSTRUMENT_SOFTWARE.equals(cmd)) {
            metaDataContainer.add(instrumentProcMetadataPanel, BorderLayout.CENTER);
        } else if (IDENTIFICATION_METADATA.equals(cmd)) {
            metaDataContainer.add(identificationMetadataPanel, BorderLayout.CENTER);
        }
        metaDataContainer.revalidate();
        metaDataContainer.repaint();
    }

    private void createContainer() {
        metaDataContainer = new JPanel();
        metaDataContainer.setLayout(new BorderLayout());
        metaDataContainer.setBackground(Color.white);
    }

    private void createTopPanel(GeneralMetaDataGroup metaDataGroup) {
        metaDataTopPanel = new JPanel(new BorderLayout());
        metaDataTopPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));


        // create tool bar
        createToolbar(metaDataGroup);
        metaDataTopPanel.add(metaDataControlBar, BorderLayout.CENTER);

        // create help button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.white);
        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.general");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));

        buttonPanel.add(helpButton);
        metaDataTopPanel.add(buttonPanel, BorderLayout.EAST);

        metaDataContainer.add(metaDataTopPanel, BorderLayout.NORTH);
    }


    private void createToolbar(GeneralMetaDataGroup metaDataGroup) {
        metaDataControlBar = new JPanel();
        metaDataControlBar.setBackground(Color.white);
        metaDataControlBar.setLayout(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup buttonGroup = new ButtonGroup();

        JToggleButton generalButton = new JToggleButton(GENERAL);
        generalButton.setActionCommand(GENERAL);
        generalButton.setPreferredSize(new Dimension(200, 25));

        JToggleButton proSamButton = new JToggleButton(SAMPLE_PROTOCOL);
        proSamButton.setActionCommand(SAMPLE_PROTOCOL);
        proSamButton.setPreferredSize(new Dimension(200, 25));
        if (!metaDataGroup.hasSampleProtocolMetadata()) {
            proSamButton.setEnabled(false);
        }

        JToggleButton insSofButton = new JToggleButton(INSTRUMENT_SOFTWARE);
        insSofButton.setActionCommand(INSTRUMENT_SOFTWARE);
        insSofButton.setPreferredSize(new Dimension(200, 25));
        if (!metaDataGroup.hasMzGraphMetadata()) {
            insSofButton.setEnabled(false);
        }

        JToggleButton identButton = new JToggleButton(IDENTIFICATION_METADATA);
        identButton.setActionCommand(IDENTIFICATION_METADATA);
        identButton.setPreferredSize(new Dimension(200, 25));
        if (!metaDataGroup.hasIdentificationMetadata()) {
            identButton.setEnabled(false);
        }

        generalButton.addActionListener(this);
        proSamButton.addActionListener(this);
        insSofButton.addActionListener(this);
        identButton.addActionListener(this);

        buttonGroup.add(generalButton);
        buttonGroup.add(proSamButton);
        buttonGroup.add(insSofButton);
        buttonGroup.add(identButton);

        metaDataControlBar.add(generalButton);
        metaDataControlBar.add(proSamButton);
        metaDataControlBar.add(insSofButton);
        metaDataControlBar.add(identButton);

        // set default selection
        generalButton.setSelected(true);
    }

    private void createMetaDataPanels(GeneralMetaDataGroup metaData) {
        generalMetadataPanel = new GeneralMetadataPanel(metaData);
        if (metaData.getSampleList() != null || metaData.getProtocol() != null) {
            sampleProtocolMetadataPanel = new SampleProtocolMetadataPanel(metaData);
        }
        if (metaData.hasMzGraphMetadata()) {
            instrumentProcMetadataPanel = new InstrumentProcessingMetadataPanel(metaData);
        }
        if (metaData.hasIdentificationMetadata()) {
            identificationMetadataPanel = new IdentificationMetadataPanel(metaData);
        }
        // set default panel
        metaDataContainer.add(generalMetadataPanel, BorderLayout.CENTER);
    }

}