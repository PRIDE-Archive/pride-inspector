package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Format a decimal number to scientific format is above/below certain threshold
 * It also formats regular decimal number into more readable format.
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ScientificNumberRenderer extends JLabel implements TableCellRenderer {

    public ScientificNumberRenderer() {
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setHorizontalAlignment(JLabel.CENTER);

        String str = null;
        if (value != null) {
            if (((Number) value).doubleValue() > Constants.MAX_NON_SCIENTIFIC_NUMBER
                    || ((Number) value).doubleValue() < Constants.MIN_MON_SCIENTIFIC_NUMBER) {
                str = Constants.LARGE_DECIMAL_NUMBER_FORMATTER.format(value);
            } else {
                str = Constants.DECIMAL_FORMATTER.format(value);
            }
        }

        this.setText(str);
        return this;
    }
}

