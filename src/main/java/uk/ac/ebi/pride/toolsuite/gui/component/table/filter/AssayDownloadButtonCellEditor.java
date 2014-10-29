package uk.ac.ebi.pride.toolsuite.gui.component.table.filter;

import javax.swing.*;
import java.awt.*;

/**
 * @author Rui Wang
 * @version $Id$
 *
 */
public class AssayDownloadButtonCellEditor extends ButtonCellEditor {
    private Object value;

    public AssayDownloadButtonCellEditor(String text, Icon icon) {
        super(text, icon);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.value = value;
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
//            SampleMetaDataDialog dialog = new SampleMetaDataDialog(((App)App.getInstance()).getMainFrame(), (DataFile)value);
//            dialog.setLocationRelativeTo(((App)App.getInstance()).getMainFrame());
//            dialog.setVisible(true);
            System.out.println("ahahah");
        }
        return super.getCellEditorValue();
    }
}
