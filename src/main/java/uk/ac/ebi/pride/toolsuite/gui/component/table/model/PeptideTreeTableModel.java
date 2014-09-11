package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import org.jdesktop.swingx.tree.TreeModelSupport;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.utilities.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import javax.swing.tree.TreePath;
import java.util.*;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class PeptideTreeTableModel extends AbstractTreeTableModel implements TaskListener<Void, Tuple<TableContentType, Object>> {

    public enum TableHeader {
        PEPTIDE_COLUMN("Peptide", "Peptide Sequence"),
        RANKING("Ranking", "Ranking"),
        PROTEIN_ACCESSION_COLUMN("Protein", "Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
        PROTEIN_STATUS("Status", "Status Of The Protein Accession"),
        PROTEIN_SEQUENCE_COVERAGE("Coverage", "Protein Sequence Coverage"),
        PEPTIDE_FIT("Fit", "Peptide Sequence Fit In Protein Sequence"),
        PRECURSOR_CHARGE_COLUMN("Charge", "Precursor Charge"),
        DELTA_MASS_COLUMN("Delta m/z", "Delta m/z [Experimental m/z - Theoretical m/z]"),
        PRECURSOR_MZ_COLUMN("Precursor m/z", "Precursor m/z"),
        PEPTIDE_MODIFICATION_COLUMN("Modifications", "Post translational modifications"),
        NUMBER_OF_FRAGMENT_IONS_COLUMN("# Ions", "Number of Fragment Ions"),
        PEPTIDE_SEQUENCE_LENGTH_COLUMN("Length", "Length"),
        SEQUENCE_START_COLUMN("Start", "Start Position"),
        SEQUENCE_END_COLUMN("Stop", "Stop Position"),
        THEORITICAL_ISOELECTRIC_POINT_COLUMN("pI", "Theoritical isoelectric point"),
        SPECTRUM_ID("Spectrum", "Spectrum Reference"),
        IDENTIFICATION_ID("Identification ID", "Identification ID"),
        PEPTIDE_ID("Peptide ID", "Peptide ID"),
        ADDITIONAL("More", "Additional Details");

        private final String header;
        private final String toolTip;

        private TableHeader(String header, String tooltip) {
            this.header = header;
            this.toolTip = tooltip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    private final Map<String, String> columnNames;
    private int rankingThreshold;
    private final Map<Tuple<String, String>, PeptideRow> spectrumIdToPeptideRow;

    public PeptideTreeTableModel(Collection<CvTermReference> listPeptideScores, int rankingThreshold) {
        super(new PeptideRow());
        this.columnNames = new LinkedHashMap<String, String>();
        this.rankingThreshold = rankingThreshold;
        this.spectrumIdToPeptideRow = Collections.synchronizedMap(new LinkedHashMap<Tuple<String, String>, PeptideRow>());
        addAdditionalColumns(listPeptideScores);
    }

    private void addAdditionalColumns(Collection<CvTermReference> listPeptideScores) {
        // add columns for search engine scores
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
            if (listPeptideScores != null && TableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader().equals(header.getHeader())) {
                for (CvTermReference scoreCvTerm : listPeptideScores) {
                    String name = scoreCvTerm.getName();
                    columnNames.put(name, name);
                }
            }
        }
    }

    public void setRankingThreshold(int rankingThreshold) {
        if (rankingThreshold != this.rankingThreshold) {
            this.rankingThreshold = rankingThreshold;

            int rankingIndex = getColumnIndex(TableHeader.RANKING.getHeader());
            List<PeptideRow> childrenToAdd = new ArrayList<PeptideRow>();
            for (PeptideRow peptideRow : spectrumIdToPeptideRow.values()) {
                int ranking = (Integer)peptideRow.getContentByIndex(rankingIndex);
                if (ranking <= rankingThreshold) {
                    childrenToAdd.add(peptideRow);
                }
            }

            ((PeptideRow)getRoot()).clearChildren();
            ((PeptideRow)getRoot()).addChildren(childrenToAdd);

            modelSupport.fireTreeStructureChanged(new TreePath(root));
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    public int getColumnIndex(String header) {
        int index = -1;

        List<Map.Entry<String, String>> entries = new LinkedList<Map.Entry<String, String>>(columnNames.entrySet());

        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().equals(header)) {
                index = entries.indexOf(entry);
            }
        }

        return index;
    }

    public String getColumnName(int index) {
        String columnName = null;

        List<Map.Entry<String, String>> entries = new LinkedList<Map.Entry<String, String>>(columnNames.entrySet());
        Map.Entry<String, String> entry = entries.get(index);
        if (entry != null) {
            columnName = entry.getKey();
        }

        return columnName;
    }

    public String getColumnTooltip(int index) {
        String tooltip = null;

        List<Map.Entry<String, String>> entries = new LinkedList<Map.Entry<String, String>>(columnNames.entrySet());
        Map.Entry<String, String> entry = entries.get(index);
        if (entry != null) {
            tooltip = entry.getValue();
        }

        return tooltip;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (getRoot().equals(node) || node == null || !(node instanceof PeptideRow)) {
            return null;
        }

        PeptideRow peptideRow = (PeptideRow) node;
        return peptideRow.getContentByIndex(column);
    }

    @Override
    public Object getChild(Object parent, int index) {
        PeptideRow parentPeptideRow = (PeptideRow) parent;
        return parentPeptideRow.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        PeptideRow parentPeptideRow = (PeptideRow) parent;
        return parentPeptideRow.getChildren().size();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        PeptideRow parentPeptideRow = (PeptideRow) parent;
        return parentPeptideRow.getChildIndex((PeptideRow) child);
    }

    public TreeModelSupport getTreeModelSupport() {
        return modelSupport;
    }

    public synchronized void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE.equals(type)) {
            addPeptideData(newData.getValue());
        } else if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetails(newData.getValue());
        } else if (TableContentType.PROTEIN_SEQUENCE_COVERAGE.equals(type)) {
            addSequenceCoverageData(newData.getValue());
        } else if (TableContentType.PEPTIDE_FIT.equals(type)) {
            addPeptideFitData(newData.getValue());
        } else if (TableContentType.PEPTIDE_DELTA.equals(type)) {
            addPeptideDeltaData(newData.getValue());
        } else if (TableContentType.PEPTIDE_PRECURSOR_MZ.equals(type)) {
            addPeptideMzData(newData.getValue());
        }
    }

    /**
     * Add peptide row data
     *
     * @param newData peptide data
     */
    private void addPeptideData(Object newData) {
        PeptideRow peptideRow = new PeptideRow((List<Object>) newData);

        int rankingIndex = getColumnIndex(TableHeader.RANKING.getHeader());
        int ranking = (Integer) peptideRow.getContentByIndex(rankingIndex);

        int proteinAccIndex = getColumnIndex(TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
        ProteinAccession protein = (ProteinAccession)peptideRow.getContentByIndex(proteinAccIndex);
        String proteinAccession = protein.getAccession();

        int peptideIndex = getColumnIndex(TableHeader.PEPTIDE_COLUMN.getHeader());
        String peptide = peptideRow.getContentByIndex(peptideIndex).toString().trim();

        PeptideRow parentPeptideRow = spectrumIdToPeptideRow.get(new Tuple<String, String>(peptide, proteinAccession));

        if (parentPeptideRow == null) {
            spectrumIdToPeptideRow.put(new Tuple<String, String>(peptide, proteinAccession), peptideRow);
            if (ranking <= rankingThreshold) {
                ((PeptideRow) getRoot()).addChild(peptideRow);
                int childIndex = ((PeptideRow) getRoot()).getChildIndex(peptideRow);
                modelSupport.fireChildAdded(new TreePath(getRoot()), childIndex, peptideRow);
            }
        } else {
            if (parentPeptideRow.getChildren().size() == 0) {
                PeptideRow firstPeptideRow = PeptideRow.copy(parentPeptideRow);
                parentPeptideRow.addChild(firstPeptideRow);
                emptyParentPeptideRow(parentPeptideRow);
            }
            parentPeptideRow.addChild(peptideRow);
            int childIndex = parentPeptideRow.getChildIndex(peptideRow);
            modelSupport.fireChildAdded(new TreePath(parentPeptideRow), childIndex, peptideRow);
        }
    }

    private void emptyParentPeptideRow(PeptideRow parentPeptideRow) {

        int peptideIdIndex = getColumnIndex(TableHeader.PEPTIDE_ID.getHeader());
        parentPeptideRow.setContentByIndex(peptideIdIndex, null);

        int numOfFragIonIndex = getColumnIndex(TableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader());
        parentPeptideRow.setContentByIndex(numOfFragIonIndex, null);

        int additionalIndex = getColumnIndex(TableHeader.ADDITIONAL.getHeader());
        parentPeptideRow.setContentByIndex(additionalIndex, null);

        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        parentPeptideRow.setContentByIndex(identIdIndex, null);

        int spectrumIdIndex = getColumnIndex(TableHeader.SPECTRUM_ID.getHeader());
        parentPeptideRow.setContentByIndex(spectrumIdIndex, null);

    }

    /**
     * Add protein related details
     *
     * @param newData protein detail map
     */
    protected void addProteinDetails(Object newData) {

        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) newData;

        // iterate over each row, set the protein name
        Collection<PeptideRow> peptideRows = spectrumIdToPeptideRow.values();
        for (PeptideRow parentPeptideRow : peptideRows) {
            addProteinDetailsForPeptideRow(proteins, (PeptideRow)getRoot(), parentPeptideRow);
            for (PeptideRow childPeptideRow : parentPeptideRow.getChildren()) {
                addProteinDetailsForPeptideRow(proteins, parentPeptideRow, childPeptideRow);
            }
        }
    }

    private void addProteinDetailsForPeptideRow(Map<String, Protein> proteins,
                                                PeptideRow parentPeptideRow,
                                                PeptideRow childPeptideRow) {
        // column index for mapped protein accession column
        int mappedAccIndex = getColumnIndex(TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
        // column index for protein name
        int identNameIndex = getColumnIndex(TableHeader.PROTEIN_NAME.getHeader());
        // column index for protein status
        int identStatusIndex = getColumnIndex(TableHeader.PROTEIN_STATUS.getHeader());

        Object proteinAccession = childPeptideRow.getContentByIndex(mappedAccIndex);

        if (proteinAccession != null) {

            String mappedAccession = ((ProteinAccession) proteinAccession).getMappedAccession();

            if (mappedAccession != null) {

                Protein protein = proteins.get(mappedAccession);

                if (protein != null) {

                    AnnotatedProtein annotatedProtein = new AnnotatedProtein(protein);

                    // set protein name
                    childPeptideRow.setContentByIndex(identNameIndex, annotatedProtein.getName());

                    // set protein status
                    childPeptideRow.setContentByIndex(identStatusIndex, annotatedProtein.getStatus().name());

                    // notify a row change
                    modelSupport.fireChildChanged(new TreePath(parentPeptideRow), parentPeptideRow.getChildIndex(childPeptideRow), childPeptideRow);
                }
            }
        }
    }

    /**
     * Add protein sequence coverages
     *
     * @param newData sequence coverage map
     */
    protected void addSequenceCoverageData(Object newData) {
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for protein sequence coverage
        int coverageIndex = getColumnIndex(TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());

        // map contains sequence coverage
        Map<Comparable, Double> coverageMap = (Map<Comparable, Double>) newData;

        // iterate over each row, set the protein name
        Collection<PeptideRow> peptideRows = spectrumIdToPeptideRow.values();
        for (PeptideRow parentPeptideRow : peptideRows) {
            for (PeptideRow childPeptideRow : parentPeptideRow.getChildren()) {
                Object identId = childPeptideRow.getContentByIndex(identIdIndex);
                Double coverage = coverageMap.get(identId);
                if (coverage != null) {
                    // set protein name
                    childPeptideRow.setContentByIndex(coverageIndex, coverage);
                    // notify a row change
                    modelSupport.fireChildChanged(new TreePath(parentPeptideRow), parentPeptideRow.getChildIndex(childPeptideRow), childPeptideRow);
                }
            }
        }
    }

    /**
     * Whether peptide sequence fit the protein sequence
     *
     * @param newDataValue
     */
    protected void addPeptideFitData(Object newDataValue) {
        // map contains peptide fit
        Map<Tuple<Comparable, Comparable>, Integer> peptideFits = (Map<Tuple<Comparable, Comparable>, Integer>) newDataValue;

        // iterate over each row, set the protein name
        Collection<PeptideRow> peptideRows = spectrumIdToPeptideRow.values();
        for (PeptideRow parentPeptideRow : peptideRows) {
            addPeptideFitDataToPeptideRow(peptideFits, (PeptideRow)getRoot(), parentPeptideRow);
            for (PeptideRow childPeptideRow : parentPeptideRow.getChildren()) {
                addPeptideFitDataToPeptideRow(peptideFits, parentPeptideRow, childPeptideRow);
            }
        }
    }

    private void addPeptideFitDataToPeptideRow(Map<Tuple<Comparable, Comparable>, Integer> peptideFits, PeptideRow parentPeptideRow, PeptideRow childPeptideRow) {
        // column index for peptide fit
        int peptideFitIndex = getColumnIndex(TableHeader.PEPTIDE_FIT.getHeader());
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for peptide id
        int peptideIdIndex = getColumnIndex(TableHeader.PEPTIDE_ID.getHeader());

        Comparable identId = (Comparable) childPeptideRow.getContentByIndex(identIdIndex);
        Comparable peptideId = (Comparable) childPeptideRow.getContentByIndex(peptideIdIndex);
        Integer peptideFit = peptideFits.get(new Tuple<Comparable, Comparable>(identId, peptideId));
        if (peptideFit != null) {
            // set protein name
            childPeptideRow.setContentByIndex(peptideFitIndex, peptideFit);
            // notify a row change
            modelSupport.fireChildChanged(new TreePath(parentPeptideRow), parentPeptideRow.getChildIndex(childPeptideRow), childPeptideRow);
        }
    }

    /**
     * The Delta mass between the Peptide MZ and the Spectrum MZ
     *
     * @param newDataValue
     */
    protected void addPeptideDeltaData(Object newDataValue) {

        Map<Tuple<Comparable, Comparable>, Double> peptideFits = (Map<Tuple<Comparable, Comparable>, Double>) newDataValue;

        // column index for peptide fit
        int peptideFitIndex = getColumnIndex(TableHeader.DELTA_MASS_COLUMN.getHeader());
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for peptide id
        int peptideIdIndex = getColumnIndex(TableHeader.PEPTIDE_ID.getHeader());

        // iterate over each row, set the protein name
        Collection<PeptideRow> peptideRows = spectrumIdToPeptideRow.values();
        for (PeptideRow parentPeptideRow : peptideRows) {
            for (PeptideRow childPeptideRow : parentPeptideRow.getChildren()) {
                Comparable identId = (Comparable) childPeptideRow.getContentByIndex(identIdIndex);
                Comparable peptideId = (Comparable) childPeptideRow.getContentByIndex(peptideIdIndex);
                Double peptideFit = peptideFits.get(new Tuple<Comparable, Comparable>(identId, peptideId));
                if (peptideFit != null) {
                    // set protein name
                    childPeptideRow.setContentByIndex(peptideFitIndex, peptideFit);
                    // notify a row change
                    modelSupport.fireChildChanged(new TreePath(parentPeptideRow), parentPeptideRow.getChildIndex(childPeptideRow), childPeptideRow);
                }
            }
        }
    }

    /**
     * The Precursor Mz
     *
     * @param newDataValue
     */
    protected void addPeptideMzData(Object newDataValue) {
        // map contains peptide fit
        Map<Tuple<Comparable, Comparable>, Double> peptideFits = (Map<Tuple<Comparable, Comparable>, Double>) newDataValue;

        // column index for peptide fit
        int peptideFitIndex = getColumnIndex(TableHeader.PRECURSOR_MZ_COLUMN.getHeader());
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for peptide id
        int peptideIdIndex = getColumnIndex(TableHeader.PEPTIDE_ID.getHeader());

        // iterate over each row, set the protein name
        Collection<PeptideRow> peptideRows = spectrumIdToPeptideRow.values();
        for (PeptideRow parentPeptideRow : peptideRows) {
            for (PeptideRow childPeptideRow : parentPeptideRow.getChildren()) {
                Comparable identId = (Comparable) childPeptideRow.getContentByIndex(identIdIndex);
                Comparable peptideId = (Comparable) childPeptideRow.getContentByIndex(peptideIdIndex);
                Double peptideFit = peptideFits.get(new Tuple<Comparable, Comparable>(identId, peptideId));
                if (peptideFit != null) {
                    // set protein name
                    childPeptideRow.setContentByIndex(peptideFitIndex, peptideFit);
                    // notify a row change
                    modelSupport.fireChildChanged(new TreePath(parentPeptideRow), parentPeptideRow.getChildIndex(childPeptideRow), childPeptideRow);
                }
            }
        }
    }

    @Override
    public void process(TaskEvent<List<Tuple<TableContentType, Object>>> event) {
        List<Tuple<TableContentType, Object>> newDataList = event.getValue();
        for (Tuple<TableContentType, Object> newData : newDataList) {
            addData(newData);
        }
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<Void> event) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }

    private static class PeptideRow {
        private List<Object> contents;
        private List<PeptideRow> childPeptideRows;

        private PeptideRow() {
            this(new ArrayList<Object>());
        }

        private PeptideRow(List<Object> contents) {
            this.contents = new ArrayList<Object>(contents);
            this.childPeptideRows = new ArrayList<PeptideRow>();
        }

        public static PeptideRow copy(PeptideRow peptideRow) {
            PeptideRow newPeptideRow = new PeptideRow(peptideRow.getContents());

            newPeptideRow.addChildren(peptideRow.getChildren());

            return newPeptideRow;
        }

        public List<Object> getContents() {
            return contents;
        }

        public Object getContentByIndex(int index) {
            return contents.get(index);
        }

        public void setContentByIndex(int index, Object value) {
            contents.set(index, value);
        }

        public List<PeptideRow> getChildren() {
            return childPeptideRows;
        }

        public void clearChildren() {
            childPeptideRows.clear();
        }

        public void addChildren(Collection<PeptideRow> children) {
            childPeptideRows.addAll(children);
        }

        public void addChild(PeptideRow child) {
            childPeptideRows.add(child);
        }

        public void removeChild(PeptideRow child) {
            childPeptideRows.remove(child);
        }

        public void setChild(int index, PeptideRow child) {
            childPeptideRows.set(index, child);
        }

        public int getChildIndex(PeptideRow child) {
            return childPeptideRows.indexOf(child);
        }
    }
}
