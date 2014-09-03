package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import uk.ac.ebi.pride.toolsuite.gui.component.sequence.PeptideFitState;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Cell renderer for the peptide present column
 * <p/>
 * User: rwang
 * Date: 22/06/11
 * Time: 10:33
 */
public class PeptideFitCellRenderer extends JLabel implements TableCellRenderer {

    private Integer state;
    private boolean isSelected;

    public PeptideFitCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.state = value == null ? PeptideFitState.UNKNOWN : (Integer) value;
        this.isSelected = isSelected;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        int width = getWidth();
        int height = getHeight();

        // rendering hints
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // paint a background
        Color background = null;

        // text to display
        String text;

        switch (state) {
            case PeptideFitState.NOT_FIT:
                background = Constants.NOT_FIT_PEPTIDE_BACKGROUND_COLOUR;
                text = Constants.NOT_FIT;
                break;
            case PeptideFitState.FIT:
                background = Constants.FIT_PEPTIDE_BACKGROUND_COLOUR;
                text = Constants.FIT;
                break;
            case PeptideFitState.STRICT_FIT:
                background = Constants.STRICT_FIT_PEPTIDE_BACKGROUND_COLOUR;
                text = Constants.STRICT_FIT;
                break;
            default:
                text = Constants.UNKNOWN;
                break;

        }

        // highlight selected
        if (isSelected) {
            background = Constants.PEPTIDE_HIGHLIGHT_COLOUR;
        }

        if (background != null) {
            g2.setColor(background);
            g2.fillRect(0, 0, width, height);
        }

        // paint text
        g2.setColor(Color.black);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
        FontMetrics fontMetrics = g2.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(text);
        int xPos = (width - textWidth) / 2;
        int yPos = height / 2 + fontMetrics.getDescent() + 2;
        g2.drawString(text, xPos, yPos);

        g2.dispose();
    }
}
