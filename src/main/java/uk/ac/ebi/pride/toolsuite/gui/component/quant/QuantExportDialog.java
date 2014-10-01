package uk.ac.ebi.pride.toolsuite.gui.component.quant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableRow;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.utilities.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.QuantProteinTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.ExportTableDataTask;
import uk.ac.ebi.pride.toolsuite.gui.url.HttpUtilities;
import uk.ac.ebi.pride.toolsuite.gui.utils.EnsemblSpeciesMapper;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.utilities.term.CvTermReference;
import uk.ac.ebi.pride.util.NumberUtilities;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.DOT;
import static uk.ac.ebi.pride.toolsuite.gui.utils.Constants.TAB_SEP_FILE;
/*
 * Created by JFormDesigner on Mon Aug 22 14:56:28 BST 2011
 */


/**
 * @author ypriverol
 * @author rwang
 */
public class QuantExportDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(QuantExportDialog.class);

    private final static String UP_REGULATED = "Up Regulated";
    private final static String DOWN_REGULATED = "Down Regulated";

    private PrideInspectorContext appContext;
    private JTable table;
    private DataAccessController controller;
    private Collection<CvTermReference> listProteinScores;

    public QuantExportDialog(Frame owner, JTable table, DataAccessController controller, Collection<CvTermReference> listProteinScores) {
        super(owner);
        this.table = table;
        this.controller = controller;
        this.listProteinScores = listProteinScores;
        setupMainPane();
        initComponents();
        populateComponents();
    }

    private void setupMainPane() {
        this.appContext = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        this.setTitle(appContext.getProperty("export.quantitative.data.long.title"));
        ImageIcon dialogIcon = (ImageIcon) GUIUtilities.loadIcon(appContext.getProperty("export.quantitative.data.small.icon"));
        this.setIconImage(dialogIcon.getImage());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        scrollPane2 = new JScrollPane();
        proteinTable = createProteinTable(listProteinScores);
        ensemblButton = new JButton();
        exportButton = new JButton();
        label5 = new JLabel();
        reactomeButton = new JButton();
        panel2 = new JPanel();
        reagentLabel = new JLabel();
        reagentComboBox = new JComboBox();
        regulationLabel = new JLabel();
        regulationComboBox = new JComboBox();
        percentageLabel = new JLabel();
        percentageTextField = new JTextField();
        filterButton = new JButton();
        closeButton = new JButton();
        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.export.quant");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));

        //======== this ========
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder(null);

            //======== scrollPane2 ========
            {
                scrollPane2.setViewportView(proteinTable);
            }

            //---- ensemblButton ----
            ensemblButton.setText("Ensembl View");

            //---- exportButton ----
            exportButton.setText("Export");

            //---- label5 ----
            label5.setText("Protein");

            //---- reactomeButton ----
            reactomeButton.setText("Reactome Pathway");

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addGap(8, 8, 8)
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(label5, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(panel1Layout.createParallelGroup()
                                                            .addComponent(ensemblButton, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                                                            .addComponent(exportButton, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                                                            .addComponent(reactomeButton, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))))
                                    .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                    .addComponent(label5)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel1Layout.createParallelGroup()
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                    .addGap(65, 65, 65)
                                                    .addComponent(exportButton)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(ensemblButton)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(reactomeButton))
                                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                                    .addGap(6, 6, 6)
                                                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)))
                                    .addContainerGap())
            );
        }

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder("Filter"));

            //---- reagentLabel ----
            reagentLabel.setText("Reagent Ratio");

            //---- regulationLabel ----
            regulationLabel.setText("Regulation");

            //---- percentageLabel ----
            percentageLabel.setText("Percentage (%)");

            //---- filterButton ----
            filterButton.setText("Filter");

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                    panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                    .addGap(12, 12, 12)
                                    .addGroup(panel2Layout.createParallelGroup()
                                            .addComponent(reagentLabel)
                                            .addComponent(reagentComboBox, 0, 205, Short.MAX_VALUE))
                                    .addGap(25, 25, 25)
                                    .addGroup(panel2Layout.createParallelGroup()
                                            .addComponent(regulationComboBox, 0, 165, Short.MAX_VALUE)
                                            .addComponent(regulationLabel))
                                    .addGap(24, 24, 24)
                                    .addGroup(panel2Layout.createParallelGroup()
                                            .addGroup(panel2Layout.createSequentialGroup()
                                                    .addComponent(percentageTextField, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(filterButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                            .addComponent(percentageLabel))
                                    .addContainerGap())
            );
            panel2Layout.setVerticalGroup(
                    panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                    .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(reagentLabel)
                                            .addComponent(regulationLabel)
                                            .addComponent(percentageLabel))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel2Layout.createParallelGroup()
                                            .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(filterButton)
                                                    .addComponent(regulationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(percentageTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addComponent(reagentComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        //---- closeButton ----
        closeButton.setText("Close");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addGroup(contentPaneLayout.createParallelGroup()
                                                        .addComponent(panel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(closeButton, GroupLayout.Alignment.TRAILING))
                                                .addContainerGap())
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addComponent(helpButton)
                                                .addGap(8, 8, 8))))
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addComponent(helpButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(closeButton)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void populateComponents() {
        // filter options

        // configure protein table
        proteinTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        proteinTable.setRowSelectionAllowed(false);
        proteinTable.setColumnSelectionAllowed(true);
        proteinTable.setSelectionForeground(Color.red);
        proteinTable.setSelectionBackground(Color.red);

        // get all the reagents
        populateReagentComboBox();

        // get all the regulations
        regulationComboBox.addItem(UP_REGULATED);
        regulationComboBox.addItem(DOWN_REGULATED);

        // ensembl button action
        ensemblButton.addActionListener(new EnsemblActionListener());

        // reactome button action
        reactomeButton.addActionListener(new ReactomePathwayListener());

        // export button action
        exportButton.addActionListener(new ExportActionListener());

        // filter button action
        filterButton.addActionListener(new FilterActionListener());

        // close button action
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuantExportDialog.this.dispose();
            }
        });

    }

    /**
     * Populate the protein table with initial values
     *
     * @return JTable  a new jtable
     */
    private JTable createProteinTable(Collection<CvTermReference> listProteinScores) {
        QuantProteinTableModel tableModel = new QuantProteinTableModel(listProteinScores);
        tableModel.removeAllColumns();

        // get the existing quant protein table model
        QuantProteinTableModel existingTableModel = (QuantProteinTableModel) table.getModel();

        // get compre column index
        int compareColumnIndex = existingTableModel.getColumnIndex(ProteinTableHeader.COMPARE.getHeader());

        // copy all the columns
        int colCnt = existingTableModel.getColumnCount();
        for (int i = 0; i < colCnt; i++) {
            if (i != compareColumnIndex) {
                tableModel.addColumn(existingTableModel.getColumnName(i), existingTableModel.getColumnTooltip(i));
            }
        }

        // copy all the data
        int rowCnt = existingTableModel.getRowCount();
        for (int i = 0; i < rowCnt; i++) {
            ProteinTableRow row = (ProteinTableRow) existingTableModel.getRow(i);
            //row.remove(compareColumnIndex);
            tableModel.addRow(row);
        }

        return TableFactory.createQuantProteinTable(controller, tableModel);
    }

    /**
     * Set the initial values of the reagent combo box
     */
    private void populateReagentComboBox() {
        TableModel tableModel = table.getModel();
        int colCnt = tableModel.getColumnCount();
        for (int i = 0; i < colCnt; i++) {
            String header = tableModel.getColumnName(i);
            if (header.contains("/")) {
                reagentComboBox.addItem(header);
                filterButton.setEnabled(true);
            }
        }
    }


    /**
     * This is a quick implementation for SLING deliverables
     * <p/>
     * NOTE: it is a not very nice implementation
     */
    private class ReactomePathwayListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int rowCnt = proteinTable.getRowCount();
            if (rowCnt > 0) {
                try {
                    File tmpHtmlFile = createTemporaryHtml();
                    if (tmpHtmlFile.exists()) {
                        HttpUtilities.openURL("file://" + tmpHtmlFile.getAbsolutePath());
                    }
                } catch (IOException e1) {
                    // show warning message
                    Runnable code = new Runnable() {
                        @Override
                        public void run() {
                            GUIUtilities.warn(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent(),
                                    "Failed to send query to Reactome", "Reactome Pathway Expression");
                        }
                    };

                    try {
                        EDTUtils.invokeAndWait(code);
                    } catch (InvocationTargetException e2) {
                        logger.error("Failed to show warning message", e1);
                    } catch (InterruptedException e2) {
                        logger.error("Failed to show warning message", e1);
                    }
                }
            }
        }


        private File createTemporaryHtml() throws IOException {
            File tmpHtmlFile = File.createTempFile("reactom", ".html");

            // get template file
            InputStream inputStream = QuantExportDialog.class.getClassLoader().getResourceAsStream(appContext.getProperty("reactome.html.template"));

            // create query value
            String value = createQueryValue();

            // read from template file
            BufferedReader reader = null;
            PrintWriter writer = null;

            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                writer = new PrintWriter(new FileWriter(tmpHtmlFile));

                String line;
                String placeHolder = appContext.getProperty("reactome.query.value.placeholder");
                while ((line = reader.readLine()) != null) {
                    if (line.contains(placeHolder)) {
                        line = line.replace(placeHolder, value);
                    }
                    writer.println(line);
                }

            } finally {
                if (reader != null) {
                    reader.close();
                }

                if (writer != null) {
                    writer.close();
                }
            }

            return tmpHtmlFile;
        }

        private String createQueryValue() {
            String value = "";
            String separator = ",";

            int rowCnt = proteinTable.getRowCount();
            if (rowCnt > 0) {
                int protColIndex = -1;
                int quantDataStartColIndex = -1;
                int colCnt = proteinTable.getColumnCount();
                for (int i = 0; i < colCnt; i++) {
                    String colName = proteinTable.getColumnName(i);
                    if (colName.equals(ProteinTableHeader.PROTEIN_ACCESSION.getHeader())) {
                        protColIndex = i;
                    }
                }

                if (protColIndex < colCnt - 1) {
                    quantDataStartColIndex = protColIndex + 1;
                }

                // add header
                value += proteinTable.getColumnName(protColIndex) + separator;
                for (int i = quantDataStartColIndex; i < colCnt; i++) {
                    value += proteinTable.getColumnName(i) + separator;
                }
                value = value.substring(0, value.length() - 1) + "\n";

                for (int i = 0; i < rowCnt; i++) {
                    Object protein = proteinTable.getValueAt(i, protColIndex);
                    String prot = null;
                    if (protein == null) {
                        prot = ((ProteinAccession) protein).getMappedAccession();
                    }

                    if (prot != null && quantDataStartColIndex > -1) {
                        value += prot + separator;
                        for (int j = quantDataStartColIndex; j < colCnt; j++) {
                            String colName = proteinTable.getColumnName(j);
                            if (colName.contains("/")) {
                                Double quantVal = (Double) proteinTable.getValueAt(i, j);
                                if (quantVal != null) {
                                    value += quantVal;
                                }
                                value += separator;
                            }
                        }
                        value = value.substring(0, value.length() - 1) + "\n";
                    }
                }
            }

            return value;
        }
    }

    /**
     * Triggered when ensembl button is clicked
     */
    private class EnsemblActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int rowCnt = proteinTable.getRowCount();
            if (rowCnt > 0) {
                int protColIndex = -1;
                int colCnt = proteinTable.getColumnCount();
                for (int i = 0; i < colCnt; i++) {
                    String colName = proteinTable.getColumnName(i);
                    if (colName.equals(ProteinTableHeader.PROTEIN_ACCESSION.getHeader())) {
                        protColIndex = i;
                    }
                }

                if (protColIndex >= 0) {
                    String url = getBaseURL();
                    if (url != null) {
                        for (int i = 0; i < rowCnt; i++) {
                            Object protein = proteinTable.getValueAt(i, protColIndex);
                            String prot = null;
                            if (protein == null) {
                                prot = ((ProteinAccession) protein).getMappedAccession();
                            }

                            if (prot != null) {
                                url += ";id=" + prot;
                            }
                        }
                        HttpUtilities.openURL(url);
                    } else {
                        // show warning message
                        Runnable code = new Runnable() {

                            @Override
                            public void run() {
                                GUIUtilities.warn(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent(),
                                        "Experiment species is not supported by Ensembl Karyotype", "Unknown Species");
                            }
                        };
                        try {
                            EDTUtils.invokeAndWait(code);
                        } catch (InvocationTargetException e1) {
                            logger.error("Failed to show warning message", e1);
                        } catch (InterruptedException e1) {
                            logger.error("Failed to show warning message", e1);
                        }
                    }
                }
            }
        }

        /**
         * Get the base url to connect to Ensembl karyotype
         *
         * @return String  url string
         */
        private String getBaseURL() {
            String url = null;

            try {
                ExperimentMetaData metaData = controller.getExperimentMetaData();
                java.util.List<String> speciesIds = DataAccessUtilities.getTaxonomy(metaData);
                if (speciesIds.size() > 0) {
                    url = appContext.getProperty("ensembl.genome.browser.url");
                    String ensemblSpeciesName = EnsemblSpeciesMapper.getInstance().getEnsemblName(speciesIds.get(0));
                    url = String.format(url, ensemblSpeciesName);
                }
            } catch (DataAccessException e) {
                logger.error("Failed to retrieve metadata", e);
            }

            return url;
        }
    }

    /**
     * Triggered when export button is clicked
     */
    private class ExportActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            DataAccessController controller = appContext.getForegroundDataAccessController();
            String defaultFileName = controller.getName().split("\\" + DOT)[0] + "_quantitative_data";
            SimpleFileDialog ofd = new SimpleFileDialog(appContext.getOpenFilePath(), "Export Quantitative Data", true, defaultFileName, false, TAB_SEP_FILE);
            ofd.setMultiSelectionEnabled(false);
            int result = ofd.showDialog(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent(), null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = ofd.getSelectedFile();
                // store file path for reuse
                String filePath = selectedFile.getPath();
                appContext.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
                ExportTableDataTask newTask = new ExportTableDataTask(proteinTable,
                        filePath + (filePath.endsWith(TAB_SEP_FILE) ? "" : TAB_SEP_FILE),
                        "Export Protein Quantification", "Export Protein Quantification");
                TaskUtil.startBackgroundTask(newTask);
            }
        }
    }

    /**
     * Triggered when filter button is clicked
     */
    private class FilterActionListener implements ActionListener {
        private String label;

        private FilterActionListener() {
            label = percentageLabel.getText();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // valid the input
            String input = percentageTextField.getText();

            if (NumberUtilities.isNumber(input)) {
                float percentage = Float.parseFloat(input);
                if (percentage < 0) {
                    setWarningMessage();
                } else {
                    // set the original message
                    removeWarningMessage();
                    // add filter to table
                    addFilter(percentage);
                    // highlight selected reagent column
                    highlightColumn();

                }
            } else {
                setWarningMessage();
            }
        }

        private void addFilter(float percentage) {
            // get reagent
            String reagent = (String) reagentComboBox.getSelectedItem();


            // get regulation
            String regulation = (String) regulationComboBox.getSelectedItem();
            boolean upRegulated = regulation.equals(UP_REGULATED);

            // get table model
            TableModel tableModel = proteinTable.getModel();
            RowSorter rowSorter = proteinTable.getRowSorter();
            if (rowSorter == null || !(rowSorter instanceof TableRowSorter)) {
                rowSorter = new NumberTableRowSorter(tableModel);
                table.setRowSorter(rowSorter);
            }
            ((TableRowSorter) rowSorter).setRowFilter(new ReagentRowFilter(reagent, percentage, upRegulated));

        }

        private void highlightColumn() {
            // get reagent
            String reagent = (String) reagentComboBox.getSelectedItem();
            // get the column index
            int columnIndex = -1;
            int colCnt = proteinTable.getColumnCount();
            for (int i = 0; i < colCnt; i++) {
                if (proteinTable.getColumnName(i).equals(reagent)) {
                    columnIndex = i;
                }
            }
            proteinTable.setColumnSelectionInterval(columnIndex, columnIndex);
        }

        private void setWarningMessage() {
            percentageLabel.setText("<html>" + label + "<div style=\"color:#FF0000\">Positive Number Only</div></html>");
        }

        private void removeWarningMessage() {
            percentageLabel.setText(label);
        }
    }

    /**
     * Reagent row filter
     */
    private class ReagentRowFilter extends RowFilter<Object, Object> {

        private int columnIndex;
        private boolean upRegulated;
        private float value;

        private ReagentRowFilter(String reagent, float percentage, boolean upRegulated) {
            // get the column index
            TableModel tableModel = proteinTable.getModel();
            int colCnt = tableModel.getColumnCount();
            for (int i = 0; i < colCnt; i++) {
                if (tableModel.getColumnName(i).equals(reagent)) {
                    this.columnIndex = i;
                }
            }
            // calculate reference value
            this.upRegulated = upRegulated;
            if (upRegulated) {
                this.value = 1 + 1 * (percentage / 100);
            } else {
                this.value = 1 - 1 * (percentage / 100);
            }
        }

        @Override
        public boolean include(Entry<?, ?> entry) {
            Object entryValue = entry.getValue(columnIndex);

            if (entryValue != null) {
                float floatValue = Float.parseFloat(entryValue.toString());
                if ((upRegulated && floatValue >= value) || (!upRegulated && floatValue <= value)) {
                    return true;
                }
            }

            return false;
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JScrollPane scrollPane2;
    private JTable proteinTable;
    private JButton ensemblButton;
    private JButton exportButton;
    private JLabel label5;
    private JButton reactomeButton;
    private JPanel panel2;
    private JLabel reagentLabel;
    private JComboBox reagentComboBox;
    private JLabel regulationLabel;
    private JComboBox regulationComboBox;
    private JLabel percentageLabel;
    private JTextField percentageTextField;
    private JButton filterButton;
    private JButton closeButton;
    private JButton helpButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

