package uk.ac.ebi.pride.toolsuite.gui.component.dialog;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.AssayFileDownloadTableModel;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.GetAssayFileMetadataTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for download files belongs to assay
 *
 * @author Rui Wang
 * @version $Id$
 */
public class AssayFileDownloadDialog extends JDialog implements ActionListener {
    private static final String CANCEL_ACTION_COMMAND = "cancelAction";
    private static final String DOWNLOAD_ACTION_COMMAND = "downloadAction";

    /**
     * File mapping table
     */
    private JTable fileDownloadSelectionTable;

    /**
     * Accession for the assay to download
     */
    private final String assayAccession;

    /**
     * Application context
     */
    private final DesktopContext prideInspectorContext;

    /**
     * File mapping table model
     */
//    private FileMappingTableModel fileMappingTableModel;

    public AssayFileDownloadDialog(Frame owner, String assayAccession) {
        super(owner);
        this.assayAccession = assayAccession;
        this.prideInspectorContext = PrideInspector.getInstance().getDesktopContext();
        initComponents();
        postComponents();
    }

    public AssayFileDownloadDialog(Dialog owner, String assayAccession) {
        super(owner);
        this.assayAccession = assayAccession;
        this.prideInspectorContext = PrideInspector.getInstance().getDesktopContext();
        initComponents();
        postComponents();
    }

    /**
     * Create GUI components
     */
    private void initComponents() {
        this.setSize(new Dimension(600, 300));

        JPanel contentPanel = new JPanel(new BorderLayout());
        this.setContentPane(contentPanel);

        // create table panel
        initTablePanel();

        // create button panel
        initControlPanel();

        this.setContentPane(contentPanel);
    }

    /**
     * Post component creation, populate the table with content
     */
    private void postComponents() {
        AssayFileDownloadTableModel model = (AssayFileDownloadTableModel) fileDownloadSelectionTable.getModel();

        GetAssayFileMetadataTask task = new GetAssayFileMetadataTask(assayAccession);
        task.addTaskListener(model);
        TaskUtil.startBackgroundTask(task);
    }

    /**
     * Initialize table panel
     */
    private void initTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // create table title label
        JLabel label = new JLabel("File Download (assay: " + assayAccession + ")");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        tablePanel.add(label, BorderLayout.NORTH);

        // create table
        fileDownloadSelectionTable = TableFactory.createAssayFileDownloadTable();

        // scroll pane
        JScrollPane scrollPane = new JScrollPane(fileDownloadSelectionTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        this.getContentPane().add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Initialize control panel
     */
    private void initControlPanel() {
        // setup main pane
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        // control pane
        JPanel ctrlPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(90, 30));
        cancelButton.setActionCommand(CANCEL_ACTION_COMMAND);
        cancelButton.addActionListener(this);
        ctrlPane.add(cancelButton);

        // next button
        JButton addButton = new JButton("Download");
        addButton.setPreferredSize(new Dimension(90, 30));
        addButton.setActionCommand(DOWNLOAD_ACTION_COMMAND);
        addButton.addActionListener(this);
        ctrlPane.add(addButton);

        controlPanel.add(ctrlPane, BorderLayout.EAST);

        this.getContentPane().add(controlPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String evtName = e.getActionCommand();

        if (CANCEL_ACTION_COMMAND.equals(evtName)) {
            this.dispose();
        } else if (DOWNLOAD_ACTION_COMMAND.equals(evtName)) {

            this.dispose();
        }
    }
}
