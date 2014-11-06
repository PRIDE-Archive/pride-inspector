package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.utilities.util.Tuple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for file mapping
 *
 * @author Rui Wang
 * @version $Id$
 */
public class AssayFileDownloadTableModel extends ProgressiveListTableModel<Void, Tuple<FileDetail, Boolean>> {
    public enum TableHeader {
        SELECTION("+", "Select a file to download"),
        FILE_NAME("File Name", "File name"),
        TYPE("Type", "File type"),
        SIZE("Size(M)", "File size in megabyte");

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


    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(Tuple<FileDetail, Boolean> newData) {
        // row count
        int rowCnt = this.getRowCount();

        // add a new row
        contents.add(newData);
        fireTableRowsInserted(rowCnt, rowCnt);
    }

    public List<Tuple<FileDetail, Boolean>> getData() {
        return (List<Tuple<FileDetail, Boolean>>) new ArrayList(contents);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return TableHeader.SELECTION.getHeader().equals(getColumnName(columnIndex)) || super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (rowIndex < 0 || columnIndex < 0 || rowIndex >= getRowCount() || columnIndex >= TableHeader.values().length) {
            return null;
        }


        Tuple<FileDetail, Boolean> entry = (Tuple<FileDetail, Boolean>) getRow(rowIndex);
        FileDetail fileDetail = entry.getKey();
        Boolean selected = entry.getValue();

        if (TableHeader.SELECTION.getHeader().equals(getColumnName(columnIndex))) {
            return selected;
        } else if (TableHeader.FILE_NAME.getHeader().equals(getColumnName(columnIndex))) {
            return fileDetail.getFileName();
        } else if (TableHeader.TYPE.getHeader().equals(getColumnName(columnIndex))) {
            return fileDetail.getFileType();
        } else if (TableHeader.SIZE.getHeader().equals(getColumnName(columnIndex))) {
            long fileSizeInBytes = fileDetail.getFileSize();
            double fileSize = (fileSizeInBytes * 1.0) / (1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.###");
            return df.format(fileSize);
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Tuple<FileDetail, Boolean> entry = (Tuple<FileDetail, Boolean>) getRow(rowIndex);

        if (TableHeader.SELECTION.getHeader().equals(getColumnName(columnIndex))) {
            entry.setValue((Boolean) aValue);
        } else {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }
}
