package uk.ac.ebi.pride.toolsuite.gui.component.ws;

import org.jdesktop.swingx.JXTable;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.AssayTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProjectTableModel;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.GetAssayMetadataTask;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.GetProjectMetadataTask;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class PrideArchiveWSSearchPane extends JPanel {

    private final PrideInspectorContext appContext;
    private JButton searchButton;
    private JTextField searchField;
    private JXTable projectDetailTable;
    private JXTable assayDetailTable;


    public PrideArchiveWSSearchPane() {
        this.appContext = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();

        // setup the main panel
        setupMainPane();

        // add the rest of the components
        addComponents();

        // perform the initial search
        search();

    }

    /**
     * Configure the main display panel
     */
    private void setupMainPane() {
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Add all the components
     */
    private void addComponents() {
        // search panel
        JPanel searchPanel = new JPanel();
        setupSearchPanel(searchPanel);

        // content panel
        JPanel contentPanel = new JPanel();
        setupContentPanel(contentPanel);

        // add the main components
        this.add(searchPanel, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Setup the search panel
     *
     * @param searchPanel search panel
     */
    private void setupSearchPanel(JPanel searchPanel) {
        searchPanel.setLayout(new GridBagLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // search box panel
        JPanel searchBoxPanel = new JPanel();
        searchBoxPanel.setOpaque(false);
        searchBoxPanel.setLayout(new FlowLayout());

        // add search button
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        searchButton.setPreferredSize(new Dimension(100, 40));
        searchButton.setFont(searchButton.getFont().deriveFont(15f));
        searchBoxPanel.add(searchButton, FlowLayout.LEFT);

        // add search text field
        searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    search();
                }
            }
        });
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(searchField.getFont().deriveFont(14f));
        searchBoxPanel.add(searchField, FlowLayout.LEFT);


        searchPanel.add(searchBoxPanel);
    }

    /**
     * Setup the content panel
     *
     * @param contentPanel content panel
     */
    private void setupContentPanel(JPanel contentPanel) {
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);

        // project detail Panel
        JPanel projectDetailPanel = new JPanel(new BorderLayout());
        projectDetailPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        projectDetailPanel.setOpaque(false);

        // project detail label
        JPanel projectDetailLabelPanel = new JPanel(new BorderLayout());
        projectDetailLabelPanel.setOpaque(false);
        String projectDetail = appContext.getProperty("pride.archive.project.detail");
        JLabel projectDetailLabel = new JLabel(projectDetail);
        projectDetailLabel.setFont(projectDetailLabel.getFont().deriveFont(Font.BOLD));
        projectDetailLabel.setOpaque(false);
        projectDetailLabelPanel.add(projectDetailLabel, BorderLayout.WEST);
        projectDetailPanel.add(projectDetailLabelPanel, BorderLayout.NORTH);

        // project detail table
        projectDetailTable = TableFactory.createProjectDetailTable();
        JScrollPane projectDetailScrollPane = new JScrollPane(projectDetailTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        projectDetailScrollPane.setOpaque(false);
        projectDetailPanel.add(projectDetailScrollPane, BorderLayout.CENTER);

        // selection listener for project detail table
        ListSelectionModel projectDetailTableSelectionModel = projectDetailTable.getSelectionModel();
        projectDetailTableSelectionModel.addListSelectionListener(new ProjectSelectionListener());

        // assay detail panel
        JPanel assayDetailPanel = new JPanel(new BorderLayout());
        assayDetailPanel.setOpaque(false);

        // assay detail label
        JPanel assayDetailLabelPanel = new JPanel(new BorderLayout());
        assayDetailLabelPanel.setOpaque(false);
        String assayDetail = appContext.getProperty("pride.archive.assay.detail");
        JLabel assayDetailLabel = new JLabel(assayDetail);
        assayDetailLabel.setFont(assayDetailLabel.getFont().deriveFont(Font.BOLD));
        assayDetailLabel.setOpaque(false);
        assayDetailLabelPanel.add(assayDetailLabel, BorderLayout.WEST);
        assayDetailPanel.add(assayDetailLabelPanel, BorderLayout.NORTH);

        // assay detail table
        assayDetailTable = TableFactory.createAssayDetailTable();
        JScrollPane assayDetailScrollPane = new JScrollPane(assayDetailTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        assayDetailScrollPane.setOpaque(false);
        assayDetailPanel.add(assayDetailScrollPane, BorderLayout.CENTER);

        // layout protein detail and assay detail panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerSize(0);
        splitPane.setResizeWeight(0.7);
        splitPane.setOpaque(false);

        splitPane.setTopComponent(projectDetailPanel);
        splitPane.setBottomComponent(assayDetailPanel);

        contentPanel.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Search to retrieve the results
     */
    private void search() {
        // get the text for search field
        String searchTerm = searchField.getText().trim();

        // clear assay table model
        AssayTableModel assayDetailTableModel = (AssayTableModel) assayDetailTable.getModel();
        assayDetailTableModel.removeAllRows();

        // get project table model
        ProjectTableModel projectDetailTableModel = (ProjectTableModel) projectDetailTable.getModel();
        projectDetailTableModel.removeAllRows();

        // start task
        Task task = new GetProjectMetadataTask(searchTerm);
        task.addTaskListener(projectDetailTableModel);
        TaskUtil.startBackgroundTask(task);
    }

    private class ProjectSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int rowNum = projectDetailTable.getSelectedRow();
                int rowCnt = projectDetailTable.getRowCount();
                if (rowCnt > 0 && rowNum >= 0) {
                    ProjectTableModel projectTableModel = (ProjectTableModel) projectDetailTable.getModel();

                    // get project accession
                    int projectAccessionColumn = projectTableModel.getColumnIndex(ProjectTableModel.TableHeader.ACCESSION.getHeader());
                    int projectAccessionModelRowIndex = projectDetailTable.convertRowIndexToModel(rowNum);
                    String projectAccession = (String) projectTableModel.getValueAt(projectAccessionModelRowIndex, projectAccessionColumn);

                    // get number of assays
                    int numberOfAssaysColumn = projectTableModel.getColumnIndex(ProjectTableModel.TableHeader.NUM_OF_ASSAY.getHeader());
                    int numberOfAssaysModelRowIndex = projectDetailTable.convertRowIndexToModel(rowNum);
                    int numberOfAssays = (Integer) projectTableModel.getValueAt(numberOfAssaysModelRowIndex, numberOfAssaysColumn);

                    // clear assay table model
                    AssayTableModel assayDetailTableModel = (AssayTableModel) assayDetailTable.getModel();
                    assayDetailTableModel.removeAllRows();

                    // start retrieving assay summaries
                    Task task = new GetAssayMetadataTask(projectAccession, numberOfAssays);
                    task.addTaskListener(assayDetailTableModel);
                    TaskUtil.startBackgroundTask(task);
                }
            }
        }
    }

}
