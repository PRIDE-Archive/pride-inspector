package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.pride.archive.web.service.model.project.ProjectSummary;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ProjectTableModel extends ProgressiveListTableModel<Void, ProjectSummary>{

    public enum TableHeader {
        ACCESSION ("Accession", "Project accession"),
        TITLE ("Title", "Project title"),
        SPECIES ("Species", "Species"),
        TISSUES ("Tissue", "Tissue"),
        MODIFICATION ("Modification", "Modification"),
        INSTRUMENT ("Instrument", "Instrument"),
        PROJECT_TAG ("Project Tag", "Project tag"),
        NUM_OF_ASSAY ("#Assays", "Number of assays"),
        PUBLICATION_DATE ("Publication date", "Publication date"),
        SUBMISSION_TYPE ("Submission Type", "Submission type"),
        DOWNLOAD("Download", "Download and open project files");

        private final String header;
        private final String toolTip;

        TableHeader(String header, String toolTip) {
            this.header = header;
            this.toolTip = toolTip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    @Override
    public void initializeTableModel() {
        columnNames.clear();

        for (TableHeader header : ProjectTableModel.TableHeader.values()) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(ProjectSummary newData) {
        int rowCnt = this.getRowCount();
        addRow(newData);
        fireTableRowsInserted(rowCnt, rowCnt);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || columnIndex <0 || rowIndex >= getRowCount() || columnIndex >= TableHeader.values().length) {
            return null;
        }

        ProjectSummary projectDetail = (ProjectSummary)getRow(rowIndex);
        String columnName = getColumnName(columnIndex);

        if (TableHeader.ACCESSION.getHeader().equals(columnName))  {
            return projectDetail.getAccession();
        } else if (TableHeader.TITLE.getHeader().equals(columnName)) {
            return projectDetail.getTitle();
        } else if (TableHeader.SPECIES.getHeader().equals(columnName)) {
            return StringUtils.join(projectDetail.getSpecies(), ",");
        } else if (TableHeader.TISSUES.getHeader().equals(columnName)) {
            return StringUtils.join(projectDetail.getTissues(), ",");
        } else if (TableHeader.MODIFICATION.getHeader().equals(columnName)) {
            return StringUtils.join(projectDetail.getPtmNames(), ",");
        } else if (TableHeader.INSTRUMENT.getHeader().equals(columnName)) {
            return StringUtils.join(projectDetail.getInstrumentNames(), ",");
        } else if (TableHeader.PROJECT_TAG.getHeader().equals(columnName)) {
            return StringUtils.join(projectDetail.getProjectTags(), ",");
        } else if (TableHeader.NUM_OF_ASSAY.getHeader().equals(columnName)) {
            return projectDetail.getNumAssays();
        } else if (TableHeader.PUBLICATION_DATE.getHeader().equals(columnName)) {
            return projectDetail.getPublicationDate();
        } else if (TableHeader.SUBMISSION_TYPE.getHeader().equals(columnName)) {
            return projectDetail.getSubmissionType();
        }  else if (TableHeader.DOWNLOAD.getHeader().equals(columnName)) {
            return projectDetail.getAccession();
        }

        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return TableHeader.DOWNLOAD.getHeader().equals(getColumnName(columnIndex)) || super.isCellEditable(rowIndex, columnIndex);
    }
}
