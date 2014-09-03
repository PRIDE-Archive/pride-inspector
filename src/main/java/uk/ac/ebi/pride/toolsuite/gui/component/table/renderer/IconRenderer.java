package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Cell renderer which draws a icon
 *
 * User: rwang
 * Date: 05/07/2011
 * Time: 16:26
 */
public class IconRenderer extends JLabel implements TableCellRenderer{
    private Icon icon;

    public IconRenderer(Icon icon) {
        super(icon);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
