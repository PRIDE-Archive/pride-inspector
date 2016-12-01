package uk.ac.ebi.pride.toolsuite.gui.component.table;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.table.filter.AssayDownloadButtonCellEditor;
import uk.ac.ebi.pride.toolsuite.gui.component.table.filter.ProjectDownloadButtonCellEditor;
import uk.ac.ebi.pride.toolsuite.gui.component.table.listener.*;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.*;
import uk.ac.ebi.pride.toolsuite.gui.component.table.renderer.*;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorter.ProjectTableSorter;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.ComponentHeaderRenderer;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.ComponentTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.ProteinSortableTreeTable;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.TreeTableRowSorter;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.url.*;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.*;
import uk.ac.ebi.pride.utilities.term.SearchEngineScoreCvTermReference;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * TableFactory can be used to different type of tables.
 * <p/>
 *
 * @author ypriverol
 * @author rwang
 *         Date: 11-Sep-2010
 *         Time: 13:39:00
 */
public class TableFactory {
    /**
     * Build a table to display spectrum related details
     *
     * @return JTable   spectrum table
     */
    public static JTable createSpectrumTable() {
        return createDefaultJXTable(new SpectrumTableModel());
    }

    /**
     * Build a table to display chromatogram related details.
     *
     * @return JTable   chromatogram table
     */
    public static JTable createChromatogramTable() {
        return createDefaultJXTable(new ChromatogramTableModel());
    }


    /**
     * Build a table to display peptide related details.
     *
     * @param listProteinScores List of Reference Scores
     * @return JTable   peptide table
     */
    public static JXTable createProteinTable(final Collection<SearchEngineScoreCvTermReference> listProteinScores, final boolean hasProteinGroups, DataAccessController controller) {
        JXTable table;
        if (hasProteinGroups) {
            SortableProteinTreeTableModel proteinTreeTableModel = new SortableProteinTreeTableModel(new SortableProteinNode(), listProteinScores);
            table = createDefaultJXTreeTable(proteinTreeTableModel, true);
            table.setEditable(false);

            String protAccColumnHeader = PeptideTreeTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
            table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
            table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        } else {
            table = createProteinTable(listProteinScores, controller);
        }
        return table;
    }


    /**
     * Build a table to display identification related details when protein inference is not present
     *
     * @param controller data access controller
     * @return JTable   identification table
     */
    public static JXTable createProteinTable(Collection<SearchEngineScoreCvTermReference> listProteinScores, DataAccessController controller) {

        ProteinTableModel identTableModel = new ProteinTableModel(listProteinScores);
        JXTable table = createDefaultJXTable(identTableModel);

        TableColumnExt proteinIdColumn = (TableColumnExt) table.getColumn(ProteinTableHeader.PROTEIN_ID.getHeader());
        proteinIdColumn.setVisible(false);

        TableColumnExt compareColumn = (TableColumnExt) table.getColumn(ProteinTableHeader.COMPARE.getHeader());
        compareColumn.setVisible(false);

        TableColumnExt proteinNameColumn = (TableColumnExt) table.getColumn(ProteinTableHeader.PROTEIN_NAME.getHeader());
        // set protein name width
        proteinNameColumn.setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) table.getColumn(ProteinTableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt seqCoverageColumn = (TableColumnExt) table.getColumn(ProteinTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        seqCoverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        seqCoverageColumn.setVisible(false);

        // isoelectric point column
        TableColumnExt isoelectricColumn = (TableColumnExt) table.getColumn(ProteinTableHeader.THEORETICAL_ISOELECTRIC_POINT.getHeader());
        isoelectricColumn.setVisible(false);

        // isoelectric point column
        TableColumnExt Threshold = (TableColumnExt) table.getColumn(ProteinTableHeader.IDENTIFICATION_THRESHOLD.getHeader());
        Threshold.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = ProteinTableHeader.PROTEIN_ACCESSION.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumn protAcc = table.getColumn(protAccColumnHeader);
        protAcc.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());

        // additional column
        String additionalColHeader = ProteinTableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) table.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);


        // add mouse motion listener
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, additionalColHeader));
        table.addMouseListener(new ShowParamsMouseListener(controller, table, additionalColHeader));

        return table;
    }


    public static JXTable createPeptideSpeciesTable(int defaultRankingThreshold, double minDeltaMz, double maxDeltaMz) {
        PeptideSpeciesTableModel peptideSpeciesTableModel = new PeptideSpeciesTableModel(defaultRankingThreshold, minDeltaMz, maxDeltaMz);
        JXTable table = createDefaultJXTable(peptideSpeciesTableModel);

        // peptide sequence column width
        TableColumnExt peptide = (TableColumnExt) table.getColumn(PeptideSpeciesTableModel.TableHeader.PEPTIDE_COLUMN.getHeader());
        peptide.setCellRenderer(new PeptideSequenceCellRenderer());

        // add hyper link click listener
        String protAccColumnHeader = PeptideSpeciesTableModel.TableHeader.PROTEIN_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        TableColumnExt protAcc = (TableColumnExt) table.getColumn(protAccColumnHeader);
        protAcc.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());

        // hide delta m/z error
        TableColumnExt deltaMzError = (TableColumnExt) table.getColumn(PeptideSpeciesTableModel.TableHeader.NUMBER_OF_DELTA_MZ_ERROR_COLUMN.getHeader());
        deltaMzError.setVisible(false);

        TableColumnExt clusterColumn = (TableColumnExt) table.getColumn(PeptideSpeciesTableModel.TableHeader.CLUSTER_DETAILS.getHeader());
        clusterColumn.setCellRenderer(new HyperLinkCellRenderer());
        String assayUrl = Desktop.getInstance().getDesktopContext().getProperty("cluster.url");
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, PeptideSpeciesTableModel.TableHeader.CLUSTER_DETAILS.getHeader()));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, PeptideSpeciesTableModel.TableHeader.CLUSTER_DETAILS.getHeader(), new PrefixedHyperLinkGenerator(assayUrl)));
        clusterColumn.setVisible(false);

        // hide peptide species column
        TableColumnExt peptideSpecies = (TableColumnExt) table.getColumn(PeptideSpeciesTableModel.TableHeader.PEPTIDE_SPECIES_COLUMN.getHeader());
        peptideSpecies.setCellRenderer(new PeptideSpeciesCellRenderer());
        peptideSpecies.setVisible(false);

        return table;
    }

    public static JTable createPeptideTable(TableModel tableModel, DataAccessController controller) {
        JXTable table = createDefaultJXTable(tableModel);

        // peptide sequence column renderer
        // peptide sequence column renderer
        TableColumnExt peptideColumn = (TableColumnExt) table.getColumn(PeptideTreeTableModel.TableHeader.PEPTIDE_COLUMN.getHeader());
        peptideColumn.setCellRenderer(new PeptideSequenceCellRenderer());

        //Comparsion is only done in Quantitation Experiments.
        TableColumnExt compareColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.COMPARE.getHeader());
        compareColumn.setVisible(false);

        // delta mass column
        TableColumnExt deltaMassColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.DELTA_MZ_COLUMN.getHeader());
        double minLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
        double maxLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
        DeltaMZRenderer renderer = new DeltaMZRenderer(minLimit, maxLimit);
        deltaMassColumn.setCellRenderer(renderer);

        // peptide sequence present in protein sequence
        TableColumnExt peptideFitColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.PEPTIDE_FIT.getHeader());
        peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());
        peptideFitColumn.setVisible(false);

        // hide protein id column
        TableColumnExt proteinIdColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide peptide id column
        TableColumnExt peptideIdColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.PEPTIDE_ID.getHeader());
        peptideIdColumn.setVisible(false);

        // set protein name column width
        TableColumnExt proteinNameColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt coverageColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        coverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        coverageColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumnExt protAcc = (TableColumnExt) table.getColumn(PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
        protAcc.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());

        // set peptide column width
        peptideColumn.setPreferredWidth(200);

        // hide number of fragment ions
        TableColumnExt numOfFragmentIons = (TableColumnExt) table.getColumn(PeptideTableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader());
        numOfFragmentIons.setVisible(false);

        // hide spectrum id column
        String spectrumIdHeader = PeptideTableHeader.SPECTRUM_ID.getHeader();
        TableColumnExt spectrumIdColumn = (TableColumnExt) table.getColumn(spectrumIdHeader);
        spectrumIdColumn.setVisible(false);

        TableColumnExt clusterColumn = (TableColumnExt) table.getColumn(PeptideTableHeader.CLUSTER_DETAILS.getHeader());
        clusterColumn.setCellRenderer(new HyperLinkCellRenderer());
        String assayUrl = Desktop.getInstance().getDesktopContext().getProperty("cluster.url");
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, PeptideTableHeader.CLUSTER_DETAILS.getHeader()));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, PeptideTableHeader.CLUSTER_DETAILS.getHeader(), new PrefixedHyperLinkGenerator(assayUrl)));
        clusterColumn.setVisible(false);

        // additional column
        String additionalColHeader = PeptideTableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) table.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        // add mouse motion listener
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, additionalColHeader));
        table.addMouseListener(new ShowParamsMouseListener(controller, table, additionalColHeader));

        return table;
    }

    /**
     * Build a table to display peptide related details.
     *
     * @param listPeptideScores List of Reference Scores
     * @param controller        data access controller
     * @return JTable   peptide table
     */
    public static JTable createPeptideTable(Collection<SearchEngineScoreCvTermReference> listPeptideScores, DataAccessController controller) {

        PeptideTableModel peptideTableModel = new PeptideTableModel(listPeptideScores);
        return createPeptideTable(peptideTableModel, controller);
    }

    public static JTable createPSMTable(Collection<SearchEngineScoreCvTermReference> listPeptideScores, int defaultRankingThreshold, DataAccessController controller) {
        PeptideSpeciesPSMTableModel peptideTableModel = new PeptideSpeciesPSMTableModel(listPeptideScores, defaultRankingThreshold);
        return createPeptideTable(peptideTableModel, controller);
    }

    /**
     * Build a table to display PTM related details.
     *
     * @return JTable   ptm table
     */
    public static JTable createPTMTable() {
        PTMTableModel tableModel = new PTMTableModel();
        JXTable table = createDefaultJXTable(tableModel);

        // add hyper link click listener
        String modAccColumnHeader = PTMTableModel.TableHeader.PTM_ACCESSION.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, modAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, modAccColumnHeader, new PTMHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumnExt ptmColumn = (TableColumnExt) table.getColumn(PTMTableModel.TableHeader.PTM_ACCESSION.getHeader());
        ptmColumn.setCellRenderer(new HyperLinkCellRenderer());

        return table;
    }

    /**
     * Create a table to show a list of references
     *
     * @param references a list of input references
     * @return JTable  reference table
     */
    public static JTable createReferenceTable(Collection<Reference> references) {
        ReferenceTableModel tableModel = new ReferenceTableModel(references);
        JXTable referenceTable = createDefaultJXTable(tableModel);

        // pubmed
        String pubMedColumnHeader = ReferenceTableModel.TableHeader.PUBMED.getHeader();
        TableColumnExt pubMedColumn = (TableColumnExt) referenceTable.getColumn(pubMedColumnHeader);
        Pattern pubmedPattern = Pattern.compile("[\\d,]+");
        pubMedColumn.setCellRenderer(new HyperLinkCellRenderer(pubmedPattern));
        pubMedColumn.setMaxWidth(100);

        // doi
        String doiColumnHeader = ReferenceTableModel.TableHeader.DOI.getHeader();
        TableColumnExt doiColumn = (TableColumnExt) referenceTable.getColumn(doiColumnHeader);
        doiColumn.setCellRenderer(new HyperLinkCellRenderer());
        doiColumn.setMaxWidth(100);

        // reference
        String referenceColumnHeader = ReferenceTableModel.TableHeader.REFERENCE_DESCRIPTION.getHeader();

        // add mouse motion listener
        referenceTable.addMouseMotionListener(new TableCellMouseMotionListener(referenceTable, pubMedColumnHeader, doiColumnHeader, referenceColumnHeader));
        referenceTable.addMouseListener(new HyperLinkCellMouseClickListener(referenceTable, pubMedColumnHeader, new PrefixedHyperLinkGenerator(Constants.PUBMED_URL_PERFIX), pubmedPattern));
        referenceTable.addMouseListener(new HyperLinkCellMouseClickListener(referenceTable, doiColumnHeader, new DOIHyperLinkGenerator(Constants.DOI_URL_PREFIX)));
        Collection<String> columnHeadersWithPopup = new HashSet<>();
        columnHeadersWithPopup.add(referenceColumnHeader);
        referenceTable.addMouseListener(new MouseClickPopupListener(referenceTable, columnHeadersWithPopup));

        return referenceTable;
    }

    /**
     * Create a table for showing a list of param groups
     *
     * @param paramGroups given list of param groups
     * @return JTable  param table
     */
    public static JTable createParamTable(List<ParamGroup> paramGroups) {
        ParamTableModel paramTableModel = new ParamTableModel(paramGroups);
        return createParamTable(paramTableModel);
    }

    /**
     * Create a table for showing a ParamGroup
     *
     * @param paramGroup given ParamGroup
     * @return JTable  param table
     */
    public static JTable createParamTable(ParamGroup paramGroup) {
        ParamTableModel paramTableModel = new ParamTableModel(paramGroup);
        return createParamTable(paramTableModel);
    }


    /**
     * Create a table for showing a collection parameters
     *
     * @param parameters a collection of parameters
     * @return JTable  param table
     */
    public static JTable createParamTable(Collection<Parameter> parameters) {
        ParamTableModel paramTableModel = new ParamTableModel(parameters);
        return createParamTable(paramTableModel);
    }

    /**
     * Create a table for showing a ParamTableModel
     *
     * @param paramTableModel given param table model
     * @return JTable  param table
     */
    private static JTable createParamTable(ParamTableModel paramTableModel) {
        JXTable paramTable = createDefaultJXTable(paramTableModel);

        // hyperlink ontology accessions
        String accColumnHeader = ParamTableModel.TableHeader.ACCESSION.getHeader();
        TableColumnExt accColumn = (TableColumnExt) paramTable.getColumn(accColumnHeader);
        accColumn.setCellRenderer(new HyperLinkCellRenderer());

        // add mouse motion listener
        paramTable.addMouseMotionListener(new TableCellMouseMotionListener(paramTable, accColumnHeader));
        paramTable.addMouseListener(new HyperLinkCellMouseClickListener(paramTable, accColumnHeader, new PrefixedHyperLinkGenerator(Constants.OLS_URL_PREFIX)));

        // acc column hidden
        String accHeader = ParamTableModel.TableHeader.ACCESSION.getHeader();
        TableColumnExt accCol = (TableColumnExt) paramTable.getColumn(accHeader);
        accCol.setVisible(false);


        return paramTable;
    }

    /**
     * Create a table for showing contacts
     *
     * @param contacts given list of contacts
     * @return JTable  contact table
     */
    public static JTable createContactTable(Collection<Person> contacts) {

        ContactTableModel tableModel = new ContactTableModel(contacts);
        JXTable contactTable = createDefaultJXTable(tableModel);

        // hyperlink contact emails
        String infoColumnHeader = ContactTableModel.TableHeader.INFORMATION.getHeader();
        TableColumnExt infoColumn = (TableColumnExt) contactTable.getColumn(infoColumnHeader);
        infoColumn.setCellRenderer(new HyperLinkCellRenderer());

        // add mouse motion listener
        contactTable.addMouseMotionListener(new TableCellMouseMotionListener(contactTable, infoColumnHeader));
        contactTable.addMouseListener(new HyperLinkCellMouseClickListener(contactTable, infoColumnHeader, new EmailHyperLinkGenerator()));

        return contactTable;
    }

    /**
     * Create a table for quantitative sample data
     *
     * @param sample quantitative sample
     * @return JTable  Quantitative table
     */
    public static JTable createQuantSampleTable(QuantitativeSample sample) {
        QuantSampleTableModel tableModel = new QuantSampleTableModel(sample);
        return createDefaultJXTable(tableModel);
    }

    /**
     * Create a table for quantitative protein data with a given table model
     *
     * @param tableModel quant protein table model
     * @return JTable  quant protein table
     */
    public static JTable createQuantProteinTable(DataAccessController controller, TableModel tableModel) {

        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable quantProteinTable = createDefaultJXTable(tableModel);
        quantProteinTable.setAutoCreateColumnsFromModel(false);
        // add table model change listener
        tableModel.addTableModelListener(new BarChartColumnListener(quantProteinTable));

        // in case the compare doesn't exist
        List<TableColumn> columns = columnModel.getColumns(true);
        for (TableColumn column : columns) {
            if (column.getHeaderValue().equals(ProteinTableHeader.COMPARE.getHeader())) {
                column.setMaxWidth(25);
            }
        }
        // hide mapped protein accession
        String mappedProtAccHeader = ProteinTableHeader.PROTEIN_ACCESSION.getHeader();
        TableColumnExt mappedProtAccColumn = (TableColumnExt) quantProteinTable.getColumn(mappedProtAccHeader);
        mappedProtAccColumn.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());
        // add hyper link click listener
        quantProteinTable.addMouseMotionListener(new TableCellMouseMotionListener(quantProteinTable, mappedProtAccHeader));
        quantProteinTable.addMouseListener(new HyperLinkCellMouseClickListener(quantProteinTable, mappedProtAccHeader, new ProteinAccHyperLinkGenerator()));

        TableColumnExt proteinIdColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.PROTEIN_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide the protein name column
        TableColumnExt proteinNameColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt seqCoverageColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        seqCoverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        seqCoverageColumn.setVisible(false);

        // isoelectric point column
        TableColumnExt isoelectricColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.THEORETICAL_ISOELECTRIC_POINT.getHeader());
        isoelectricColumn.setVisible(false);

        // threshold
        TableColumnExt proteinThresholdColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.IDENTIFICATION_THRESHOLD.getHeader());
        proteinThresholdColumn.setVisible(false);

        // number of peptides
        TableColumnExt numOfPeptideColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.NUMBER_OF_PEPTIDES.getHeader());
        numOfPeptideColumn.setVisible(false);

        // number of unique peptides
        TableColumnExt numOfUniquePeptideColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.NUMBER_OF_UNIQUE_PEPTIDES.getHeader());
        numOfUniquePeptideColumn.setVisible(false);

        // number of ptms
        TableColumnExt numOfPtmColumn = (TableColumnExt) quantProteinTable.getColumn(ProteinTableHeader.NUMBER_OF_PTMS.getHeader());
        numOfPtmColumn.setVisible(false);

        // additional column
        String additionalColHeader = ProteinTableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) quantProteinTable.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        if(controller.getType().equals(DataAccessController.Type.MZTAB)){
            for(int i = 0; i < quantProteinTable.getColumnModel().getColumnCount(); i++){
                TableColumnExt column = (TableColumnExt) quantProteinTable.getColumn(i);
                if(column.getTitle().contains(QuantProteinTableModel.ABUNDANCE_HEADER)){
                    column.setVisible(false);
                    i--;
                }
                if (column.getTitle().contains(Constants.QUANTIFICATION_RATIO_CHAR) && !column.getTitle().contains(QuantProteinTableModel.ABUNDANCE_HEADER)) {
                    TableCellRenderer renderer = new BarChartRenderer(2, 0, 1);
                    column.setCellRenderer(renderer);
                }
            }

        }

        // add mouse motion listener
        quantProteinTable.addMouseMotionListener(new TableCellMouseMotionListener(quantProteinTable, additionalColHeader));
        quantProteinTable.addMouseListener(new ShowParamsMouseListener(controller, quantProteinTable, additionalColHeader));

        return quantProteinTable;
    }

    /**
     * Create a table for protein quantitative data
     *
     * @param controller data access controller
     * @return JTable   protein quantitative table
     */
    public static JTable createQuantProteinTable(DataAccessController controller, Collection<SearchEngineScoreCvTermReference> listProteinScores) {
        QuantProteinTableModel tableModel = new QuantProteinTableModel(listProteinScores);
        return createQuantProteinTable(controller, tableModel);
    }

    /**
     * Create a table for protein quantitative data
     *
     * @param controller data access controller
     * @return JTable   protein quantitative table
     */
    public static JTable createQuantProteinTable(DataAccessController controller, Collection<SearchEngineScoreCvTermReference> listProteinScores, Map<Comparable, StudyVariable> studyVariables) {
        QuantProteinTableModel tableModel = new QuantProteinTableModel(listProteinScores, studyVariables);
        return createQuantProteinTable(controller, tableModel);
    }

    /**
     * Create a table for peptide quantitative data
     *
     * @param listPeptideScores List of CvTerm
     * @return JTable  peptide table
     */
    public static JTable createQuantPeptideTable(DataAccessController controller, Collection<SearchEngineScoreCvTermReference> listPeptideScores, Map<Comparable, StudyVariable> studyVariables) {

        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        QuantPeptideTableModel tableModel = new QuantPeptideTableModel(listPeptideScores, studyVariables);
        JXTable quantPeptideTable = createDefaultJXTable(tableModel);
        quantPeptideTable.setAutoCreateColumnsFromModel(false);

        // add table model change listener
        tableModel.addTableModelListener(new BarChartColumnListener(quantPeptideTable));

        // in case the compare doesn't exist
        List<TableColumn> columns = columnModel.getColumns(true);
        for (TableColumn column : columns) {
            if (column.getHeaderValue().equals(ProteinTableHeader.COMPARE.getHeader())) {
                column.setMaxWidth(25);
            }
        }

        // hide mapped protein accession
        String mappedProtAccHeader = PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        TableColumnExt mappedProtAccColumn = (TableColumnExt) quantPeptideTable.getColumn(mappedProtAccHeader);
        mappedProtAccColumn.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());
        // add hyper link click listener
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, mappedProtAccHeader));
        quantPeptideTable.addMouseListener(new HyperLinkCellMouseClickListener(quantPeptideTable, mappedProtAccHeader, new ProteinAccHyperLinkGenerator()));
        // hide protein name
        TableColumnExt proteinNameColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setVisible(false);

        // hide protein status
        TableColumnExt proteinStatusColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // hide protein sequence coverage
        TableColumnExt proteinSeqCoverageColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        proteinSeqCoverageColumn.setVisible(false);

        // peptide sequence present in protein sequence
        TableColumnExt peptideFitColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PEPTIDE_FIT.getHeader());
        peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());
        peptideFitColumn.setVisible(false);

        // precursor charge column
        TableColumnExt precursorChargeColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PRECURSOR_CHARGE_COLUMN.getHeader());
        precursorChargeColumn.setVisible(false);

        // delta mass column
        TableColumnExt deltaMassColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.DELTA_MZ_COLUMN.getHeader());
        deltaMassColumn.setVisible(false);
        double minLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
        double maxLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
        DeltaMZRenderer renderer = new DeltaMZRenderer(minLimit, maxLimit);
        deltaMassColumn.setCellRenderer(renderer);

        // precursor m/z column
        TableColumnExt precursorMzColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PRECURSOR_MZ_COLUMN.getHeader());
        precursorMzColumn.setVisible(false);

        // hide number of fragment ions
        TableColumnExt fragIonsColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader());
        fragIonsColumn.setVisible(false);

        // hide peptide sequence length
        TableColumnExt seqLengthColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PEPTIDE_SEQUENCE_LENGTH_COLUMN.getHeader());
        seqLengthColumn.setVisible(false);

        // hide sequence start
        TableColumnExt sequenceStartColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.SEQUENCE_START_COLUMN.getHeader());
        sequenceStartColumn.setVisible(false);

        // hide sequence end
        TableColumnExt sequenceEndColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.SEQUENCE_END_COLUMN.getHeader());
        sequenceEndColumn.setVisible(false);

        // hide spectrum id
        TableColumnExt spectrumIdColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.SPECTRUM_ID.getHeader());
        spectrumIdColumn.setVisible(false);

        // hide protein id column
        TableColumnExt proteinIdColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide peptide id column
        TableColumnExt peptideIdColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PEPTIDE_ID.getHeader());
        peptideIdColumn.setVisible(false);

        // hide peptide charge state
        TableColumnExt rankingColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.RANKING.getHeader());
        rankingColumn.setVisible(false);

        // additional column
        String additionalColHeader = PeptideTableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) quantPeptideTable.getColumn(additionalColHeader);
        Icon detailIcon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(detailIcon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        if(controller.getType().equals(DataAccessController.Type.MZTAB)){
            for(int i = 0; i < quantPeptideTable.getColumnModel().getColumnCount(); i++){
                TableColumnExt column = (TableColumnExt) quantPeptideTable.getColumn(i);
                if(column.getTitle().contains(QuantPeptideTableModel.ABUNDANCE_HEADER)){
                    column.setVisible(false);
                    i--;
                }
                if (column.getTitle().contains(Constants.QUANTIFICATION_RATIO_CHAR) && !column.getTitle().contains(QuantPeptideTableModel.ABUNDANCE_HEADER)) {
                    TableCellRenderer rendererBar = new BarChartRenderer(2, 0, 1);
                    column.setCellRenderer(rendererBar);
                }
            }
        }

        // add mouse motion listener
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, additionalColHeader));
        quantPeptideTable.addMouseListener(new ShowParamsMouseListener(controller, quantPeptideTable, additionalColHeader));

        return quantPeptideTable;
    }

    /**
     * Create a table for peptide quantitative data
     *
     * @param listPeptideScores List of CvTerm
     * @return JTable  peptide table
     */
    public static JTable createQuantPeptideTable(DataAccessController controller, Collection<SearchEngineScoreCvTermReference> listPeptideScores) {

        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        QuantPeptideTableModel tableModel = new QuantPeptideTableModel(listPeptideScores);
        JXTable quantPeptideTable = createDefaultJXTable(tableModel);
        quantPeptideTable.setAutoCreateColumnsFromModel(false);

        // add table model change listener
        tableModel.addTableModelListener(new BarChartColumnListener(quantPeptideTable));

        // in case the compare doesn't exist
        List<TableColumn> columns = columnModel.getColumns(true);
        for (TableColumn column : columns) {
            if (column.getHeaderValue().equals(ProteinTableHeader.COMPARE.getHeader())) {
                column.setMaxWidth(25);
            }
        }

        // hide mapped protein accession
        String mappedProtAccHeader = PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        TableColumnExt mappedProtAccColumn = (TableColumnExt) quantPeptideTable.getColumn(mappedProtAccHeader);
        mappedProtAccColumn.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());
        // add hyper link click listener
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, mappedProtAccHeader));
        quantPeptideTable.addMouseListener(new HyperLinkCellMouseClickListener(quantPeptideTable, mappedProtAccHeader, new ProteinAccHyperLinkGenerator()));
        // hide protein name
        TableColumnExt proteinNameColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setVisible(false);

        // hide protein status
        TableColumnExt proteinStatusColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // hide protein sequence coverage
        TableColumnExt proteinSeqCoverageColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        proteinSeqCoverageColumn.setVisible(false);

        // peptide sequence present in protein sequence
        TableColumnExt peptideFitColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PEPTIDE_FIT.getHeader());
        peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());
        peptideFitColumn.setVisible(false);

        // precursor charge column
        TableColumnExt precursorChargeColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PRECURSOR_CHARGE_COLUMN.getHeader());
        precursorChargeColumn.setVisible(false);

        // delta mass column
        TableColumnExt deltaMassColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.DELTA_MZ_COLUMN.getHeader());
        deltaMassColumn.setVisible(false);
        double minLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
        double maxLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
        DeltaMZRenderer renderer = new DeltaMZRenderer(minLimit, maxLimit);
        deltaMassColumn.setCellRenderer(renderer);

        // precursor m/z column
        TableColumnExt precursorMzColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PRECURSOR_MZ_COLUMN.getHeader());
        precursorMzColumn.setVisible(false);

        // hide number of fragment ions
        TableColumnExt fragIonsColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader());
        fragIonsColumn.setVisible(false);

        // hide peptide sequence length
        TableColumnExt seqLengthColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PEPTIDE_SEQUENCE_LENGTH_COLUMN.getHeader());
        seqLengthColumn.setVisible(false);

        // hide sequence start
        TableColumnExt sequenceStartColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.SEQUENCE_START_COLUMN.getHeader());
        sequenceStartColumn.setVisible(false);

        // hide sequence end
        TableColumnExt sequenceEndColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.SEQUENCE_END_COLUMN.getHeader());
        sequenceEndColumn.setVisible(false);

        // hide spectrum id
        TableColumnExt spectrumIdColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.SPECTRUM_ID.getHeader());
        spectrumIdColumn.setVisible(false);

        // hide protein id column
        TableColumnExt proteinIdColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide peptide id column
        TableColumnExt peptideIdColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.PEPTIDE_ID.getHeader());
        peptideIdColumn.setVisible(false);

        // hide peptide charge state
        TableColumnExt rankingColumn = (TableColumnExt) quantPeptideTable.getColumn(PeptideTableHeader.RANKING.getHeader());
        rankingColumn.setVisible(false);

        // additional column
        String additionalColHeader = PeptideTableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) quantPeptideTable.getColumn(additionalColHeader);
        Icon detailIcon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(detailIcon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        // add mouse motion listener
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, additionalColHeader));
        quantPeptideTable.addMouseListener(new ShowParamsMouseListener(controller, quantPeptideTable, additionalColHeader));

        return quantPeptideTable;
    }

    private static JXTable createDefaultJXTable(TableModel tableModel) {
        JXTable table = new JXTable();

        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        table.setColumnModel(columnModel);

        if (tableModel != null) {
            table.setModel(tableModel);
        }

        configureTable(table);

        return table;
    }

    private static ProteinSortableTreeTable createDefaultJXTreeTable(SortableProteinTreeTableModel tableModel, boolean hasProteinGropus) {
        //final JXTreeTable table = new JXTreeTable();
        ProteinSortableTreeTable table = new ProteinSortableTreeTable(tableModel, hasProteinGropus);
        String[] columnHeaderTooltips = new String[tableModel.getColumnTooltips().size()];
        for (int i = 0; i < tableModel.getColumnTooltips().size(); i++)
            columnHeaderTooltips[i] = tableModel.getColumnTooltips().get(i);
        // Install component header
        TableColumnModel tcm = table.getColumnModel();
        final ComponentTableHeader ch = new ComponentTableHeader(tcm, columnHeaderTooltips);
        table.setTableHeader(ch);

        // Install mouse listeners in header for right-click popup capabilities
        MouseAdapter ma = createHeaderMouseAdapter(table);

        ch.addMouseListener(ma);
        ch.addMouseMotionListener(ma);

        PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();

        Icon plusIcon = GUIUtilities.loadImageIcon(context.getProperty("plus.icon.small"));
        Icon minusIcon = GUIUtilities.loadImageIcon(context.getProperty("minus.icon.small"));



        table.setTreeCellRenderer((new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree,
                                                          Object value, boolean selected, boolean expanded,
                                                          boolean isLeaf, int row, boolean focused) {

                Component c = super.getTreeCellRendererComponent(tree,value,selected,expanded,isLeaf,row,focused);

                Border paddingBorder = BorderFactory.createEmptyBorder(0, 5, 0, 0);

                this.setBorder(paddingBorder);

                return c;

            }
        }));

        table.setOverwriteRendererIcons(true);
        table.setCollapsedIcon(plusIcon);

        table.setExpandedIcon(minusIcon);

        table.setClosedIcon(null);
        table.setLeafIcon(null);
        table.setOpenIcon(null);

        configureTreeTable(table);

        return table;
    }

    private static void configureTreeTable(JXTreeTable table) {
        JPopupMenu popupMenu = new TablePopupMenu(table);

        table.setComponentPopupMenu(popupMenu);

        // selection mode
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // set column control visible
        table.setColumnControlVisible(true);

        // auto fill
        table.setFillsViewportHeight(true);

        // row height
        table.setRowHeight(20);

        // remove border
        table.setBorder(BorderFactory.createEmptyBorder());

        ((AbstractTableModel) table.getModel()).fireTableStructureChanged();
        table.setRootVisible(false);
        table.setSortable(false);
    }


    private static void configureTable(JXTable table) {
        JPopupMenu popupMenu = new TablePopupMenu(table);

        table.setComponentPopupMenu(popupMenu);

        // selection mode
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // set column control visible
        table.setColumnControlVisible(true);

        // auto fill
        table.setFillsViewportHeight(true);

        // sorter
        NumberTableRowSorter numberTableRowSorter = new NumberTableRowSorter(table.getModel());
        table.setRowSorter(numberTableRowSorter);

        // row height
        table.setRowHeight(20);

        // prevent dragging of column
        table.getTableHeader().setReorderingAllowed(false);

        // remove border
        table.setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Creates and configures a mouse adapter for tree table headers to display a context menu.
     *
     * @return the header mouse adapter
     */
    public static MouseAdapter createHeaderMouseAdapter(final JXTreeTable treeTbl) {
        // TODO: maybe integrate functionality into tree table class
        final ComponentTableHeader ch = (ComponentTableHeader) treeTbl.getTableHeader();
        MouseAdapter ma = new MouseAdapter() {
            /**
             * The column view index of the last pressed column header.<br>
             */
            private int col = -1;

            /**
             * Creates and configures the current column header's context menu.
             * @return the context menu
             */
            private JPopupMenu createPopup() {
                JPopupMenu popup = new JPopupMenu() {
                    @Override
                    public void setVisible(boolean b) {
                        // automatically raise the column header when popup is dismissed
                        if (!b) {
                            raise();
                        }
                        super.setVisible(b);
                    }
                };

                // Create sub-menu containing sorting-related items
                JMenu sortMenu = new JMenu("Sort");
                //		sortMenu.setIcon(IconConstants.SORT_ICON);

                int modelCol = treeTbl.convertColumnIndexToModel(col);
                SortOrder order = ((TreeTableRowSorter) treeTbl.getRowSorter()).getSortOrder(modelCol);
                JMenuItem ascChk = new JRadioButtonMenuItem("Ascending", order == SortOrder.ASCENDING);
                JMenuItem desChk = new JRadioButtonMenuItem("Descending", order == SortOrder.DESCENDING);
                JMenuItem unsChk = new JRadioButtonMenuItem("Unsorted", order == SortOrder.UNSORTED);

                ActionListener sortListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        SortOrder order = SortOrder.valueOf(
                                ((AbstractButton) evt.getSource()).getText().toUpperCase());
                        ((TreeTableRowSorter) treeTbl.getRowSorter()).setSortOrder(
                                treeTbl.convertColumnIndexToModel(col), order);
                    }
                };
                ascChk.addActionListener(sortListener);
                desChk.addActionListener(sortListener);
                unsChk.addActionListener(sortListener);

                sortMenu.add(ascChk);
                sortMenu.add(desChk);
                sortMenu.add(unsChk);

                ActionListener aggrListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        TableColumnExt column = treeTbl.getColumnExt(col);
                    }
                };
                popup.add(sortMenu);
                return popup;
            }

            @Override
            public void mousePressed(MouseEvent me) {
                int col = ch.columnAtPoint(me.getPoint());
                if (!treeTbl.isSortable())
                    col = -1;
                // Check whether right mouse button has been pressed
                if ((col != -1) && (me.getButton() == MouseEvent.BUTTON1)) {
                    this.col = col;
                    lower();
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if ((me.getButton() == MouseEvent.BUTTON3) &&
                        (ch.getBounds().contains(me.getPoint()))) {
                    // don't show popup for web resources column
                    if (!" ".equals(treeTbl.getColumn(this.col).getIdentifier())) {
                        this.createPopup().show(ch, ch.getHeaderRect(this.col).x - 1, ch.getHeight() - 1);
                    } else {
                        this.raise();
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                TableColumn draggedColumn = ch.getDraggedColumn();
                if (draggedColumn != null) {
                    int col = treeTbl.convertColumnIndexToView(draggedColumn.getModelIndex());
                    if ((col != -1) && (col != this.col)) {
                        this.col = col;
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent me) {
                if ((this.col != -1) &&
                        ((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)) {
                    raise();
                }
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                if ((this.col != -1) &&
                        ((me.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)) {
                    lower();
                }
            }

            /**
             * Convenience method to configure the column header to appear pressed.
             */
            private void lower() {
                TableCellRenderer hr = ch.getColumnModel().getColumn(this.col).getHeaderRenderer();
                if (hr instanceof ComponentHeaderRenderer) {
                    ComponentHeaderRenderer chr = (ComponentHeaderRenderer) hr;
                    chr.getPanel().setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.GRAY),
                            BorderFactory.createEmptyBorder(1, 1, 0, -1)));
                    chr.getPanel().setOpaque(true);
                    ch.repaint(ch.getHeaderRect(this.col));
                }
            }

            /**
             * Convenience method to configure the column header to not appear pressed.
             */
            private void raise() {
                TableCellRenderer hr = ch.getColumnModel().getColumn(this.col).getHeaderRenderer();
                if (hr instanceof ComponentHeaderRenderer) {
                    ComponentHeaderRenderer chr = (ComponentHeaderRenderer) hr;
                    chr.getPanel().setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    chr.getPanel().setOpaque(false);
                    ch.repaint(ch.getHeaderRect(this.col));
                }
            }
        };

        return ma;
    }

    public static JXTable createProjectDetailTable() {
        ProjectTableModel projectTableModel = new ProjectTableModel();
        JXTable table = createDefaultJXTable(projectTableModel);

        // add hyper link click listener
        String projectAccessionHeader = ProjectTableModel.TableHeader.ACCESSION.getHeader();

        TableColumnExt projectAccessionColumn = (TableColumnExt) table.getColumn(projectAccessionHeader);
        projectAccessionColumn.setCellRenderer(new HyperLinkCellRenderer());

        String projectUrl = Desktop.getInstance().getDesktopContext().getProperty("prider.project.url");
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, projectAccessionHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, projectAccessionHeader, new PrefixedHyperLinkGenerator(projectUrl)));

        // title column
        TableColumnExt projectTitleColumn = (TableColumnExt) table.getColumn(ProjectTableModel.TableHeader.TITLE.getHeader());
        projectTitleColumn.setPreferredWidth(200);

        // publication date
        TableColumnExt publicationDateColumn = (TableColumnExt) table.getColumn(ProjectTableModel.TableHeader.PUBLICATION_DATE.getHeader());
        publicationDateColumn.setCellRenderer(new DateRenderer());


        String clusterScore = ProjectTableModel.TableHeader.CLUSTER_SCORE.getHeader();
        TableColumnExt clusterScoreColumn = (TableColumnExt) table.getColumn(clusterScore);
        clusterScoreColumn.setCellRenderer(new IconScoreCellRender());


        //download column
        String downloadHeader = ProjectTableModel.TableHeader.DOWNLOAD.getHeader();
        TableColumnExt downloadColumn = (TableColumnExt) table.getColumn(downloadHeader);
        String downloadText = "Download";
        downloadColumn.setCellRenderer(new ButtonCellRenderer(downloadText, null));
        downloadColumn.setCellEditor(new ProjectDownloadButtonCellEditor(downloadText, null));

        downloadColumn.setMaxWidth(100);
        downloadColumn.setMinWidth(100);

        ProjectTableSorter tableRowSorter = new ProjectTableSorter(table.getModel());
        table.setRowSorter(tableRowSorter);

        return table;
    }

    public static JXTable createAssayDetailTable() {
        AssayTableModel assayTableModel = new AssayTableModel();
        JXTable table = createDefaultJXTable(assayTableModel);

        // add hyper link click listener
        String assayAccessionHeader = AssayTableModel.TableHeader.ACCESSION.getHeader();

        TableColumnExt assayAccessionColumn = (TableColumnExt) table.getColumn(assayAccessionHeader);
        assayAccessionColumn.setCellRenderer(new HyperLinkCellRenderer());

        String assayUrl = Desktop.getInstance().getDesktopContext().getProperty("prider.assay.url");
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, assayAccessionHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, assayAccessionHeader, new PrefixedHyperLinkGenerator(assayUrl)));

        // title column
        TableColumnExt assayTitleColumn = (TableColumnExt) table.getColumn(AssayTableModel.TableHeader.TITLE.getHeader());
        assayTitleColumn.setPreferredWidth(200);

        //download column
        String downloadHeader = AssayTableModel.TableHeader.DOWNLOAD.getHeader();
        TableColumnExt downloadColumn = (TableColumnExt) table.getColumn(downloadHeader);
        String downloadText = "Download";
        downloadColumn.setCellRenderer(new ButtonCellRenderer(downloadText, null));
        downloadColumn.setCellEditor(new AssayDownloadButtonCellEditor(downloadText, null));

        downloadColumn.setMaxWidth(100);
        downloadColumn.setMinWidth(100);

        return table;
    }

    public static JTable createAssayFileDownloadTable() {

        AssayFileDownloadTableModel assayFileDownloadTableModel = new AssayFileDownloadTableModel();
        JXTable table = createDefaultJXTable(assayFileDownloadTableModel);

        // set file selection column width
        TableColumnExt selectionColumn = (TableColumnExt) table.getColumn(AssayFileDownloadTableModel.TableHeader.SELECTION.getHeader());
        selectionColumn.setMaxWidth(20);
        selectionColumn.setMinWidth(20);

        // file selection checkbox
        selectionColumn.setCellRenderer(new CheckboxCellRenderer());
        DefaultCellEditor checkBoxCellEditor = new DefaultCellEditor(new JCheckBox());
        selectionColumn.setCellEditor(checkBoxCellEditor);

        // title column
        TableColumnExt assayFileNameColumn = (TableColumnExt) table.getColumn(AssayFileDownloadTableModel.TableHeader.FILE_NAME.getHeader());
        assayFileNameColumn.setPreferredWidth(200);

        // file type
        TableColumnExt assayFileTypeColumn = (TableColumnExt) table.getColumn(AssayFileDownloadTableModel.TableHeader.TYPE.getHeader());
        assayFileTypeColumn.setMaxWidth(80);

        // file size
        TableColumnExt assayFileSizeColumn = (TableColumnExt) table.getColumn(AssayFileDownloadTableModel.TableHeader.SIZE.getHeader());
        assayFileSizeColumn.setMaxWidth(80);

        // add mouse motion listener
        table.addMouseListener(new AssayFileDownloadSelectionListener(table));

        return table;
    }
}