package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @ypriverol
 */
public class ProteinGroupCellRenderer extends JLabel implements TableCellRenderer {

    public ProteinGroupCellRenderer() {
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        Border paddingBorder = BorderFactory.createEmptyBorder(100,100,100,100);

        this.setBorder(BorderFactory.createCompoundBorder(this.getBorder(),paddingBorder));

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
        // repaint the component
        this.revalidate();
        this.repaint();
        return this;
    }
}
