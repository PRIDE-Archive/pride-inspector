package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Table cell renderer to draw buttons
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ButtonCellRenderer extends JButton implements TableCellRenderer {

    public ButtonCellRenderer(String text, Icon icon) {
        super(text, icon);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        // set background
        Color alternate = UIManager.getColor("Table.alternateRowColor");
        if (row % 2 == 1) {
            this.setBackground(alternate);
        } else {
            this.setBackground(Color.WHITE);
        }

        return this;
    }
}
