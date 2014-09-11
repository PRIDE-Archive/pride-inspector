package uk.ac.ebi.pride.toolsuite.gui.component.table.model;



import org.jdesktop.swingx.treetable.TreeTableNode;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.SortableTreeTableModel;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.utilities.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.*;

/**
 * @author ypriverol
 * @author rwang
 */
public class SortableProteinTreeTableModel  extends SortableTreeTableModel
        implements TaskListener<Void, Tuple<TableContentType, Object>> {

    private final Random random;
    private final Map<Comparable, SortableProteinNode> proteinGroupIdToProteinTableRow;



/**
 * Constructs a sortable tree table model using the specified root node.
 *
 * @param root              The tree table node to be used as root.
 * @param listPeptideScores
 */
    public SortableProteinTreeTableModel(TreeTableNode root, Collection<CvTermReference> listPeptideScores) {
        super(root, listPeptideScores);
        this.random = new Random();
        proteinGroupIdToProteinTableRow = new HashMap<Comparable, SortableProteinNode>();
    }


    @Override
    public void process(TaskEvent<List<Tuple<TableContentType, Object>>> event) {
        List<Tuple<TableContentType, Object>> newDataList = event.getValue();
        for (Tuple<TableContentType, Object> newData : newDataList) {
            addData(newData);
        }
    }

    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN.equals(type)) {
            addProteinTableRow((ProteinTableRow) newData.getValue());
        } else if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetailData(newData.getValue());
        } else if (TableContentType.PROTEIN_SEQUENCE_COVERAGE.equals(type)) {
            addSequenceCoverageData(newData.getValue());
        }
    }

    private void addProteinTableRow(ProteinTableRow proteinTableRow) {

        SortableProteinNode proteinNode = new SortableProteinNode(proteinTableRow, columnNames, proteinScores);

        Comparable proteinGroupId = proteinTableRow.getProteinGroupId();

        if (proteinGroupId == null) {
            // generate an random id
            proteinGroupId = proteinTableRow.getProteinId().toString() + random.nextInt();
        }

        SortableProteinNode parentProteinTableRow = proteinGroupIdToProteinTableRow.get(proteinGroupId);

        if (parentProteinTableRow == null) {
            // add as a parent node
            proteinGroupIdToProteinTableRow.put(proteinGroupId, proteinNode);
            ((SortableProteinNode) getRoot()).addChildProteinTableRow(proteinNode);
            int childIndex = getIndexOfChild(getRoot(), proteinNode);
            insertNodeInto(proteinNode, (SortableProteinNode) getRoot(), childIndex);
        } else {
            parentProteinTableRow.addChildProteinTableRow(proteinNode);
            int childIndex = getIndexOfChild(parentProteinTableRow, proteinNode);
            insertNodeInto(proteinNode, parentProteinTableRow,childIndex);
        }

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        SortableProteinNode parentProteinRow = (SortableProteinNode) parent;
        return parentProteinRow.getChildProteinTableRows().indexOf(child);
    }

    private void addProteinDetailData(Object value) {
        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) value;

        // iterate over each row, set the protein name
        Collection<SortableProteinNode> parentProteinTableRows = proteinGroupIdToProteinTableRow.values();
        for (SortableProteinNode parentProteinTableRow : parentProteinTableRows) {
            addProteinDetailsForProteinTableRow(proteins, (SortableProteinNode) getRoot(), parentProteinTableRow);

            for (SortableProteinNode childProteinTableRow : parentProteinTableRow.getChildProteinTableRows()) {
                addProteinDetailsForProteinTableRow(proteins, parentProteinTableRow, childProteinTableRow);
            }
        }
    }

    private void addProteinDetailsForProteinTableRow(Map<String, Protein> proteins, SortableProteinNode parentProteinTableRow, SortableProteinNode childProteinTableRow) {
        Object proteinAccession = childProteinTableRow.getProteinAccession();

        if (proteinAccession != null) {

            String mappedAccession = ((ProteinAccession) proteinAccession).getMappedAccession();

            if (mappedAccession != null) {

                Protein protein = proteins.get(mappedAccession);

                if (protein != null) {
                    AnnotatedProtein annotatedProtein = new AnnotatedProtein(protein);

                    // set protein name
                    childProteinTableRow.setProteinName(annotatedProtein.getName());
                    childProteinTableRow.updatePropertyObject(columnNames,proteinScores);

                    // set protein status
                    childProteinTableRow.setProteinAccessionStatus(annotatedProtein.getStatus().name());
                    childProteinTableRow.updatePropertyObject(columnNames,proteinScores);

                }
            }
        }
    }

    /**
     * Add protein sequence coverages
     *
     * @param newData sequence coverage map
     */
    private void addSequenceCoverageData(Object newData) {
        // map contains sequence coverage
        Map<Comparable, Double> coverageMap = (Map<Comparable, Double>) newData;

        // iterate over each row, set the protein name
        Collection<SortableProteinNode> proteinTableRows = proteinGroupIdToProteinTableRow.values();
        for (SortableProteinNode parentProteinTableRow : proteinTableRows) {
            updateSequenceCoverageData(coverageMap, (SortableProteinNode) getRoot(), parentProteinTableRow);

            for (SortableProteinNode childProteinTableRow : parentProteinTableRow.getChildProteinTableRows()) {
                updateSequenceCoverageData(coverageMap, parentProteinTableRow, childProteinTableRow);
            }
        }
    }

    private void updateSequenceCoverageData(Map<Comparable, Double> coverageMap,
                                            SortableProteinNode parentProteinTableRow,
                                            SortableProteinNode childProteinTableRow) {
        Object proteinId = childProteinTableRow.getProteinId();
        Double coverage = coverageMap.get(proteinId);

        if (coverage != null) {
            // set protein name
            childProteinTableRow.setSequenceCoverage(coverage);
            childProteinTableRow.updatePropertyObject(columnNames, proteinScores);
        }
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

    @Override
    public Object getValueAt(Object node, int column) {
        if (getRoot().equals(node) || node == null || !(node instanceof SortableProteinNode)) {
            return null;
        }

        SortableProteinNode proteinNode = (SortableProteinNode) node;
        String columnName = getColumnName(column);

        if (ProteinTableHeader.COMPARE.getHeader().equals(columnName)) {
            return proteinNode.getComparisonState();
        } else if (ProteinTableHeader.PROTEIN_ACCESSION.getHeader().equals(columnName)) {
            return proteinNode.getProteinAccession();
        } else if (ProteinTableHeader.PROTEIN_NAME.getHeader().equals(columnName)) {
            return proteinNode.getProteinName();
        } else if (ProteinTableHeader.PROTEIN_STATUS.getHeader().equals(columnName)) {
            return proteinNode.getProteinAccessionStatus();
        } else if (ProteinTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(columnName)) {
            return proteinNode.getSequenceCoverage();
        } else if (ProteinTableHeader.THEORETICAL_ISOELECTRIC_POINT.getHeader().equals(columnName)) {
            return proteinNode.getIsoelectricPoint();
        } else if (ProteinTableHeader.IDENTIFICATION_THRESHOLD.getHeader().equals(columnName)) {
            return proteinNode.getThreshold();
        } else if (ProteinTableHeader.NUMBER_OF_PEPTIDES.getHeader().equals(columnName)) {
            return proteinNode.getNumberOfPeptides();
        } else if (ProteinTableHeader.NUMBER_OF_UNIQUE_PEPTIDES.getHeader().equals(columnName)) {
            return proteinNode.getNumberOfUniquePeptides();
        } else if (ProteinTableHeader.NUMBER_OF_PTMS.getHeader().equals(columnName)) {
            return proteinNode.getNumberOfPTMs();
        } else if (ProteinTableHeader.PROTEIN_ID.getHeader().equals(columnName)) {
            return proteinNode.getProteinId();
        } else if (ProteinTableHeader.PROTEIN_GROUP_ID.getHeader().equals(columnName)) {
            return proteinNode.getProteinGroupId();
        } else if (ProteinTableHeader.ADDITIONAL.getHeader().equals(columnName)) {
            return proteinNode.getProteinId();
        } else {
            return getProteinScore(proteinNode, columnName);
        }
    }

    private Double getProteinScore(SortableProteinNode proteinTableRow, String columnName) {
        List<Double> scores = proteinTableRow.getScores();

        int scoreIndex = 0;

        for (CvTermReference scoreTermReference : proteinScores) {
            if (scoreTermReference.getName().equals(columnName)) {
                return scores.get(scoreIndex);
            }
            scoreIndex++;
        }

        return null;
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
}
