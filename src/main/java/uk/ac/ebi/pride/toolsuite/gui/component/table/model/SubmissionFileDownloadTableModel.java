package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.SubmissionFileDetail;

import javax.swing.tree.TreePath;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Table model for displaying ProteomeXchange submissions
 *
 * @author Rui Wang
 * @version $Id$
 */
public class SubmissionFileDownloadTableModel extends AbstractTreeTableModel {
    private static final String NOT_APPLICABLE = "N/A";

    public enum TableHeader {

        FILE_NAME_COLUMN("File Name", "File Name"),
        FILE_SIZE_COLUMN("Size (M)", "Download File Size (M)"),
        FILE_TYPE_COLUMN("Type", "Type of the file"),
        DOWNLOAD_COLUMN("Download", "Download Option");

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

    public SubmissionFileDownloadTableModel() {
        super(new SubmissionFileDetail());
    }

    public void addSubmissionFileDetails(List<SubmissionFileDetail> submissionFileDetails) {

        for (SubmissionFileDetail sourceFileDetail : submissionFileDetails) {
            if (sourceFileDetail.getAsssayAccession() != null &&
                !sourceFileDetail.getAsssayAccession().equalsIgnoreCase(NOT_APPLICABLE) &&
                !sourceFileDetail.getFileType().equalsIgnoreCase("RESULT")) {

                for (SubmissionFileDetail resultFileDetail : submissionFileDetails) {
                    if (!sourceFileDetail.equals(resultFileDetail) &&
                        resultFileDetail.getFileType().equalsIgnoreCase("RESULT") &&
                        resultFileDetail.getAsssayAccession().equals(sourceFileDetail.getAsssayAccession())) {
                        resultFileDetail.addSourceFileMapping(sourceFileDetail);
                        sourceFileDetail.addResultFileMapping(resultFileDetail);
                    }
                }
            }
        }

        // add all the result files to root
        for (SubmissionFileDetail submissionFileDetail : submissionFileDetails) {
            if (submissionFileDetail.getAsssayAccession() == null ||
                submissionFileDetail.getAsssayAccession().equalsIgnoreCase(NOT_APPLICABLE) ||
                    submissionFileDetail.getFileType().equalsIgnoreCase("RESULT")) {
                ((SubmissionFileDetail)root).addSourceFileMapping(submissionFileDetail);
                submissionFileDetail.addResultFileMapping((SubmissionFileDetail)root);
            }
        }

        modelSupport.fireTreeStructureChanged(new TreePath(root));
    }

    @Override
    public int getColumnCount() {
        return TableHeader.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return TableHeader.values()[column].getHeader();
    }

    public int getColumnIndex(String header) {
        TableHeader[] headers = TableHeader.values();
        for (int i = 0; i < headers.length; i++) {
            TableHeader tableHeader = headers[i];
            if (tableHeader.getHeader().equals(header)) {
                return i;
            }
        }

        return -1;
    }

    public Set<Object> getNoneRootNodes() {
        Set<Object> leaves = new HashSet<Object>();

        for (SubmissionFileDetail parent : ((SubmissionFileDetail) root).getSourceFileMappings()) {
            leaves.add(parent);
            for (SubmissionFileDetail child : parent.getSourceFileMappings()) {
                leaves.add(child);
                leaves.addAll(child.getSourceFileMappings());
            }
        }

        return leaves;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (root.equals(node)) {
            return null;
        }

        TableHeader columnHeader = TableHeader.values()[column];
        SubmissionFileDetail submissionEntry = (SubmissionFileDetail) node;
        switch (columnHeader) {
            case FILE_NAME_COLUMN:
                return submissionEntry.getFileName();
            case FILE_TYPE_COLUMN:
                return submissionEntry.getFileType();
            case FILE_SIZE_COLUMN:
                double fileSize = (submissionEntry.getFileSize() * 1.0) / (1024 * 1024);
                DecimalFormat df = new DecimalFormat("#.###");
                return df.format(fileSize);
            case DOWNLOAD_COLUMN:
                return submissionEntry.isDownload();
        }
        return TableHeader.values()[column].getHeader();
    }

    @Override
    public Object getChild(Object parent, int index) {
        Object child = null;

        SubmissionFileDetail parentEntry = (SubmissionFileDetail) parent;
        List<SubmissionFileDetail> children = parentEntry.getSourceFileMappings();
        if (index >= 0 && index < children.size()) {
            child = children.get(index);
        }

        return child;
    }

    @Override
    public int getChildCount(Object parent) {
        return ((SubmissionFileDetail) parent).getSourceFileMappings().size();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        int index = -1;

        SubmissionFileDetail parentEntry = (SubmissionFileDetail) parent;
        SubmissionFileDetail childEntry = (SubmissionFileDetail) child;
        List<SubmissionFileDetail> children = parentEntry.getSourceFileMappings();
        if (children.contains(childEntry)) {
            index = children.indexOf(childEntry);
        }

        return index;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (TableHeader.values()[column].equals(TableHeader.DOWNLOAD_COLUMN)) {
            return Boolean.class;
        }
        return super.getColumnClass(column);
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {
        if (!root.equals(node) && TableHeader.values()[column].equals(TableHeader.DOWNLOAD_COLUMN) && value instanceof Boolean) {
            ((SubmissionFileDetail) node).setDownload((Boolean) value);
            List<SubmissionFileDetail> parents = ((SubmissionFileDetail) node).getResultFileMappings();
            for (SubmissionFileDetail parent : parents) {
                modelSupport.fireChildChanged(new TreePath(parent), getIndexOfChild(parent, node), node);
            }
        }
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return !root.equals(node) && TableHeader.values()[column].equals(TableHeader.DOWNLOAD_COLUMN);
    }
}