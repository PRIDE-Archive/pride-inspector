package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Cell renderer that renderer an button with given text and icon
 *
 * User: rwang
 * Date: 06/07/2011
 * Time: 10:28
 */
public class ButtonRenderer extends JButton implements TableCellRenderer{

    public ButtonRenderer() {
        this(null, null);
    }

    public ButtonRenderer(String text) {
        this(text, null);
    }

    public ButtonRenderer(String text, Icon icon) {
        super(text, icon);
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        String text = this.getText();

        if (text == null && value != null) {
            this.setText(value.toString());
        }

        return this;
    }
}
