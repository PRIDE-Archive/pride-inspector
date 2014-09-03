package uk.ac.ebi.pride.toolsuite.gui.component.report;

import javax.swing.*;
import java.awt.*;

/**
 * Label with round corners
 * User: rwang
 * Date: 07/06/11
 * Time: 10:35
 */
public class RoundCornerLabel extends JLabel {

    private Paint backgroundPaint, borderPaint;

    public RoundCornerLabel(Icon icon, String text, Paint backgroundPaint, Paint borderPaint) {
        super(text, icon, JLabel.LEFT);
        this.backgroundPaint = backgroundPaint;
        this.borderPaint = borderPaint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int x = 2;
        int y = 2;
        int width = getWidth() - 2 * x;
        int height = getHeight() - 2 * y;
        int arc = 10;

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(backgroundPaint);
        g2.fillRoundRect(x, y, width, height, arc, arc);

        // border
        g2.setStroke(new BasicStroke(1f));
        g2.setPaint(borderPaint);
        g2.drawRoundRect(x, y, width, height, arc, arc);

        // starting position
        int startX = x + arc - 5;
        int startY = y + height/2 + 5;

        // icon
        Icon icon = getIcon();
        if (icon != null) {
            g2.drawImage(((ImageIcon) icon).getImage(), startX, startY - icon.getIconHeight()/2 - 4, icon.getIconWidth(), icon.getIconHeight(), null);
        }

        // text
        String text = getText();
        if (text != null) {
            g2.setPaint(getForeground());
            int tX = (icon == null ? 0 : icon.getIconWidth() + 5) + startX;
            int tY = startY;
            g2.drawString(text, tX, tY);
        }

        g2.dispose();
    }
}
