package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class DateRenderer extends JLabel implements TableCellRenderer {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public DateRenderer() {
        this.setOpaque(true);
        this.setHorizontalAlignment(JLabel.LEFT);
        this.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        this.setText((value == null) ? "" : dateFormat.format((Date)value));

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

        this.revalidate();
        this.repaint();

        return this;
    }
}
