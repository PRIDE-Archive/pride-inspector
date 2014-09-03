package uk.ac.ebi.pride.toolsuite.gui.component.table.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: rwang
 * Date: 28/06/2011
 * Time: 14:32
 */
public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String text;
    private boolean isPushed;

    public ButtonEditor() {
        this(new JCheckBox(), null);
    }

    public ButtonEditor(JCheckBox checkBox, String text) {
        super(checkBox);
        this.button = new JButton();
        this.text = text;
        this.button.setOpaque(true);
        // add action listener
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(Color.red);
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }

        if (text == null && value != null) {
            text = value.toString();
        }

        button.setText(text);
        isPushed = true;

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        isPushed = false;
        return text;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
