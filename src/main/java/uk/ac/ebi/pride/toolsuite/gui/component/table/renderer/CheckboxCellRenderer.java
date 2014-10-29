package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Check box cell renderer draws a check box in a cell
 *
 * @author Rui Wang
 * @version $Id$
 */
public class CheckboxCellRenderer extends JCheckBox implements TableCellRenderer {
    public CheckboxCellRenderer() {
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Boolean b = (Boolean)value;
        this.setSelected(b);

        // set background
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            Color alternate = UIManager.getColor("Table.alternateRowColor");
            if (row % 2 == 1) {
                this.setBackground(alternate);
            } else {
                this.setBackground(Color.WHITE);
            }
        }

        return this;
    }
}
