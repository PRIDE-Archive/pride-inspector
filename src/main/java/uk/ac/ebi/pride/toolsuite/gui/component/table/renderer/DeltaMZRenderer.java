package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;
import uk.ac.ebi.pride.util.NumberUtilities;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Renderer for drawing delta m/a values
 * <p/>
 * User: rwang
 * Date: 07/09/2011
 * Time: 13:42
 */
public class DeltaMZRenderer extends JLabel implements TableCellRenderer {

    private double minLimit;
    private double maxLimit;
    private Double deltaMz;
    private DecimalFormat formatter;

    public DeltaMZRenderer(double min, double max) {
        setOpaque(true);
        this.minLimit = min;
        this.maxLimit = max;
        this.formatter = new DecimalFormat("#####.####");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            String vStr = value.toString();
            if (NumberUtilities.isNumber(vStr)) {
                this.deltaMz = new Double(vStr);
            }
        } else {
            this.deltaMz = null;
        }

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (deltaMz != null) {
            Graphics2D g2 = (Graphics2D) g.create();

            int width = getWidth();
            int height = getHeight();

            // rendering hints
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // paint a background
            Color background;

            if (deltaMz > maxLimit || deltaMz < minLimit) {
                background = Constants.DELTA_MZ_WARNING;
            } else {
                background = Constants.DELTA_MZ_NORMAL;
            }

            if (background != null) {
                g2.setColor(background);
                g2.fillRect(0, 0, width, height);
            }

            // paint text
            g2.setColor(Color.black);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            FontMetrics fontMetrics = g2.getFontMetrics();

            String text = formatter.format(deltaMz);
            int textWidth = fontMetrics.stringWidth(text);
            int xPos = (width - textWidth) / 2;
            int yPos = height / 2 + fontMetrics.getDescent() + 2;
            g2.drawString(text, xPos, yPos);

            g2.dispose();
        }
    }
}
