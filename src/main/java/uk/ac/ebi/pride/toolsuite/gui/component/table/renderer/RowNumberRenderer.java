package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * User: rwang
 * Date: 17-Aug-2010
 * Time: 15:33:01
 */
public class RowNumberRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setHorizontalAlignment(JLabel.CENTER);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            this.setBackground(header.getBackground());
            this.setForeground(header.getForeground());
            this.setFont(header.getFont());
        }

        if (isSelected) {
            this.setFont(this.getFont().deriveFont(Font.BOLD));
        }

        this.setText((value == null) ? "" : value.toString());
        //label.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
    }
}
