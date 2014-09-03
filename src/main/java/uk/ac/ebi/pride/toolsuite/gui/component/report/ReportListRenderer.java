package uk.ac.ebi.pride.toolsuite.gui.component.report;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;

import javax.swing.*;
import java.awt.*;

/**
 * User: rwang
 * Date: 03/06/11
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class ReportListRenderer implements ListCellRenderer {
    private static final int DEFAULT_HEIGHT = 30;
    private static final int START_ALPHA = 100;
    private static final int STOP_ALPHA = 150;

    private PrideInspectorContext context;

    public ReportListRenderer() {
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        SummaryReportMessage item = (SummaryReportMessage) value;
        SummaryReportMessage.Type type = item.getType();

        RoundCornerLabel label = new RoundCornerLabel(getIcon(type), item.getMessage(), getBackgroundPaint(type), getBorderPaint(type));
        label.setPreferredSize(new Dimension(50, DEFAULT_HEIGHT));

        return label;
    }

    /**
     * Get the icon of the message according to the type
     *
     * @param type message type
     * @return Icon    message icon
     */
    private Icon getIcon(SummaryReportMessage.Type type) {
        switch (type) {
            case SUCCESS:
                return GUIUtilities.loadIcon(context.getProperty("report.item.success.icon.small"));
            case ERROR:
                return GUIUtilities.loadIcon(context.getProperty("report.item.error.icon.small"));
            case WARNING:
                return GUIUtilities.loadIcon(context.getProperty("report.item.warning.icon.small"));
            case INFO:
                return GUIUtilities.loadIcon(context.getProperty("report.item.plain.icon.small"));
            default:
                return GUIUtilities.loadIcon(context.getProperty("report.item.plain.icon.small"));
        }
    }

    /**
     * Get the paint for the message background
     *
     * @param type message type
     * @return Paint   background
     */
    private Paint getBackgroundPaint(SummaryReportMessage.Type type) {
        switch (type) {
            case SUCCESS:
                return new GradientPaint(0, 0, new Color(40, 175, 99, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(40, 175, 99, STOP_ALPHA), true);
            case ERROR:
                return new GradientPaint(0, 0, new Color(215, 39, 41, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(215, 39, 41, STOP_ALPHA), true);
            case WARNING:
                return new GradientPaint(0, 0, new Color(251, 182, 1, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(251, 182, 1, STOP_ALPHA), true);
            case INFO:
                return new GradientPaint(0, 0, new Color(27, 106, 165, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(27, 106, 165, STOP_ALPHA), true);
            default:
                return new GradientPaint(0, 0, new Color(27, 106, 165, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(27, 106, 165, STOP_ALPHA), true);
        }
    }

    /**
     * Get the paint for the message border
     *
     * @param type message type
     * @return Paint   border color
     */
    private Paint getBorderPaint(SummaryReportMessage.Type type) {
        switch (type) {
            case SUCCESS:
                return new Color(40, 175, 99);
            case ERROR:
                return new Color(215, 39, 41);
            case WARNING:
                return new Color(251, 182, 1);
            case INFO:
                return new Color(27, 106, 165);
            default:
                return new Color(27, 106, 165);
        }
    }
}
