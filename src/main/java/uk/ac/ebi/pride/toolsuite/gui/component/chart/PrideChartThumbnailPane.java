package uk.ac.ebi.pride.toolsuite.gui.component.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.tabbedui.VerticalLayout;
import uk.ac.ebi.pride.toolsuite.chart.PrideChartType;
import uk.ac.ebi.pride.toolsuite.chart.io.ElderJSONReader;
import uk.ac.ebi.pride.toolsuite.chart.io.PrideDataReader;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.prop.PropertyManager;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: qingwei
 * Date: 09/07/13
 */
public class PrideChartThumbnailPane extends PrideChartPane {
    private ChartTabPane container;

    public PrideChartThumbnailPane(ChartTabPane container, PrideDataReader reader, PrideChartType chartType) {
        super(reader);

        this.container = container;
        drawChart(chartType);
    }

    @Override
    protected String getTitle(PrideChartType chartType) {
        return chartType.getTitle();
    }

    @Override
    protected int getTitleFontSize() {
        return 17;
    }

    @Override
    protected JPanel getMainPanel(JFreeChart chart, PrideChartType chartType) {
        return new ChartPanel(chart);
    }

    @Override
    protected JPanel getToolsPanel(final PrideChartType chartType) {
        JPanel toolBar = new JPanel();

        // get property manager
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        // Info
        Icon infoIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_info.icon.small"));
        String infoTooltip = propMgr.getProperty("chart_info.tooltip");
        PrideChartButton btnInfo = new PrideChartButton(infoIcon, infoTooltip);
        int chartID = ElderJSONReader.getChartID(chartType);
        CSH.setHelpIDString(btnInfo, "help.chart." + chartID);
        btnInfo.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        toolBar.add(btnInfo);

        // Zoom in
        Icon zoomIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_zoom_in.icon.small"));
        String zoomTooltip = propMgr.getProperty("chart_zoom_in.tooltip");
        PrideChartButton btnZoomIn = new PrideChartButton(zoomIcon, zoomTooltip);

        btnZoomIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                container.showBigView(chartType);
            }
        });
        toolBar.add(btnZoomIn);
//
//        // Auto adjust
//        Icon adjustIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_auto_adjust.icon.small"));
//        String adjustTooltip = propMgr.getProperty("chart_auto_adjust.tooltip");
//        PrideChartButton btnAdjust = new PrideChartButton(adjustIcon, adjustTooltip);
//        btnAdjust.setEnabled(cp != null);
//        btnAdjust.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                if (cp != null) cp.restoreAutoBounds();
//            }
//        });
//        toolBar.add(btnAdjust);

        toolBar.setBackground(Color.WHITE);

        return toolBar;
    }

    @Override
    protected JPanel getErrorPanel(String[] errMsgList) {
        JPanel errorPanel = new JPanel();

        errorPanel.setBackground(Color.WHITE);
        errorPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;

        JPanel center = new JPanel(new VerticalLayout());
        center.setBackground(Color.WHITE);

        // get property manager
        DesktopContext context = uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        Icon warnIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_warning.icon.small"));
        int fontSize = 12;
        JLabel iconLabel = new JLabel(warnIcon, JLabel.CENTER);
        iconLabel.setAlignmentX(0.5f);
        center.add(iconLabel);

        String msg = "This chart could not be generated because:";
        JLabel common = new JLabel(msg);
        common.setFont(new Font("Serif", Font.BOLD, fontSize));
        common.setAlignmentX(0.5f);
        center.add(common);

        for(String message : errMsgList){
            JLabel error = new JLabel(" - "+message);
            error.setFont(new Font("Serif", Font.PLAIN, fontSize));
            error.setAlignmentX(0.5f);
            center.add(error);
        }

        errorPanel.add(center, c);

        return errorPanel;
    }
}
