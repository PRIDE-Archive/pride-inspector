package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * CollapsiblePane is collapsible on mouse click.
 * <p/>
 * User: rwang
 * Date: 25-May-2010
 * Time: 09:01:15
 */
public class CollapsiblePane extends JPanel {
    private final static Color DEFAULT_TITLE_FOREGROUND = Color.black;
    private final static Color DEFAULT_BORDER_BACKGROUND = new Color(30, 30, 100, 150);
    private final static Dimension DEFAULT_MAX_DIMENSION = new Dimension(1000, 1000);
    private final String title;
    private Component contentComponent;
    private Icon expandIcon;
    private Icon collapseIcon;
    private JLabel iconLabel;

    public CollapsiblePane(String title) {
        this.title = title;
        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(DEFAULT_BORDER_BACKGROUND));
        this.setMaximumSize(DEFAULT_MAX_DIMENSION);
    }

    private void addComponents() {
        // add title label
        JComponent titleComponent = createTitleComponent();
        this.add(titleComponent, BorderLayout.PAGE_START);
    }

    private JComponent createTitleComponent() {
        JPanel panel = new GradientColorPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        // text label
        JLabel label = new JLabel(title);
        label.setOpaque(false);
        label.setForeground(DEFAULT_TITLE_FOREGROUND);
        Font font = UIManager.getDefaults().getFont("Label.font");
        label.setFont(new Font(font.getFamily(), Font.PLAIN, font.getSize() + 2));
        panel.add(label, BorderLayout.WEST);
        // icons
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        expandIcon = GUIUtilities.loadIcon(context.getProperty("navigation.expand.small.icon"));
        collapseIcon = GUIUtilities.loadIcon(context.getProperty("navigation.collapse.small.icon"));
        iconLabel = new JLabel(expandIcon);
        panel.add(iconLabel, BorderLayout.EAST);
        panel.addMouseListener(new CollapseListener());
        return panel;
    }

    public void setContentComponent(Component component) {
        this.contentComponent = component;
        this.add(contentComponent, BorderLayout.CENTER);
    }

    private class CollapseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (contentComponent != null) {
                boolean vis = !contentComponent.isVisible();
                contentComponent.setVisible(vis);
                if (vis) {
                    iconLabel.setIcon(expandIcon);
                } else {
                    iconLabel.setIcon(collapseIcon);
                }
                Component parent = CollapsiblePane.this.getParent();
                parent.repaint();
            }
        }
    }

    private static class GradientColorPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            if (!isOpaque()) {
                super.paintComponent(g);
                return;
            }

            Graphics2D ng = (Graphics2D) g.create();
            Color colour1 = getBackground().brighter();
            Color colour2 = colour1.darker();
            GradientPaint gradient = new GradientPaint(0, 0, colour1, 0, getHeight(), colour2, true);
            ng.setPaint(gradient);
            ng.fillRect(0, 0, getWidth(), getHeight());
            ng.dispose();

            setOpaque(false);
            super.paintComponent(g);
            setOpaque(true);
        }
    }
}
