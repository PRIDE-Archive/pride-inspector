package uk.ac.ebi.pride.toolsuite.gui.component.table.filter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ButtonCellEditor extends DefaultCellEditor {

    protected JButton button;
    protected boolean isPushed;


    public ButtonCellEditor(String text, Icon icon) {
        super(new JCheckBox());
        this.button = new JButton(text, icon);
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        isPushed = false;
        return super.getCellEditorValue();
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}
