package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renderer to render hyper link
 * <p/>
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 14:34:20
 */
public class HyperLinkCellRenderer extends JLabel implements TableCellRenderer {

    private Pattern pattern;
    /**
     * Whether to shorten the url
     */
    private boolean shorten;

    public HyperLinkCellRenderer() {
        this(null, false);
    }

    public HyperLinkCellRenderer(Pattern pattern) {
        this(pattern, false);
    }

    public HyperLinkCellRenderer(Pattern pattern, boolean shorten) {
        this.setOpaque(true);
        this.pattern = pattern;
        this.shorten = shorten;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        if (value != null) {
            String text = value.toString();
            boolean match = true;
            if (pattern != null) {
                // match the pattern
                Matcher m = pattern.matcher(text);
                match = m.matches();
            }

            if (match && ! value.toString().trim().equals(Constants.NOT_AVAILABLE)) {
                this.setText("<html><a href='" + text + "'>" + (shorten ? "Link" : text) + "</a>" + "</html>");
            } else {
                this.setText(text);
            }
        } else {
            this.setText(null);
        }
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