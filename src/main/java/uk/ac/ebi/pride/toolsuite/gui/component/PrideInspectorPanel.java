package uk.ac.ebi.pride.toolsuite.gui.component;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel with PRIDE logo at the bottom-right corner
 *
 * User: rwang
 * Date: 03-Nov-2010
 * Time: 09:37:41
 */
public class PrideInspectorPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        Graphics2D g2 = (Graphics2D) g;
        Composite oldComposite = g2.getComposite();
        AlphaComposite newComposite = AlphaComposite.SrcOver.derive(0.6f);
        g2.setComposite(newComposite);
        ImageIcon logoIcon = (ImageIcon) GUIUtilities.loadIcon(context.getProperty("pride.main.large.logo"));
        Dimension d = this.getSize();
        g2.drawImage(logoIcon.getImage(), (int) (d.getWidth() - logoIcon.getIconWidth() - 20), (int) (d.getHeight() - logoIcon.getIconHeight() - 20), null);
        g2.setComposite(oldComposite);
    }
}
