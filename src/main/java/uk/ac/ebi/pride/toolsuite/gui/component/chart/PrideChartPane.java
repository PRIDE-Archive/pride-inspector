package uk.ac.ebi.pride.toolsuite.gui.component.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import uk.ac.ebi.pride.toolsuite.chart.PrideChartFactory;
import uk.ac.ebi.pride.toolsuite.chart.PrideChartType;
import uk.ac.ebi.pride.toolsuite.chart.io.PrideDataException;
import uk.ac.ebi.pride.toolsuite.chart.io.PrideDataReader;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Abstract class to define the chart common characteristics.</p>
 *
 * @author Antonio Fabregat
 * @author ypriverol
 * Date: 25-ago-2010
 * Time: 15:25:59
 */
public abstract class PrideChartPane extends JPanel {

    protected PrideDataReader reader;

    /**
     * <p> Creates an instance of this ChartPane object, setting all fields as per description below.</p>
     */
    public PrideChartPane(PrideDataReader reader) {
        super(new BorderLayout());
        this.reader = reader;
    }

    public void drawChart(PrideChartType chartType) {
        if (chartType == null) {
            throw new NullPointerException("Chart Type can not set null!");
        }

        removeAll();
        setBackground(Color.WHITE);

        JFreeChart chart = PrideChartFactory.getChart(reader, chartType);
        JPanel mainPanel;
        if (chart == null) {
            PrideDataException ex = reader.getErrorMap().get(chartType);
            mainPanel = getErrorPanel(ex.getMessage().split("\n"));

        } else {
            initChart(chart);
            mainPanel = getMainPanel(chart, chartType);
        }

        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel(getTitle(chartType));
        titlePanel.add(title);
        title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, getTitleFontSize()));
        titlePanel.setBackground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        JPanel toolsPanel = getToolsPanel(chartType);
        add(toolsPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    protected abstract String getTitle(PrideChartType chartType);
    protected abstract int getTitleFontSize();
    protected abstract JPanel getMainPanel(JFreeChart chart, PrideChartType chartType);
    protected abstract JPanel getToolsPanel(PrideChartType chartType);
    protected abstract JPanel getErrorPanel(String[] errMsgList);

    private void initChart(JFreeChart chart){
        //Removing title and subtitle from the JFreeCharts
        chart.setTitle("");
        for (Object subtitle : chart.getSubtitles()) {
            //The legend is returned as a subtitle, but it has not to be deleted
            if (!(subtitle instanceof LegendTitle)) {
                chart.removeSubtitle((Title) subtitle);
            }
        }
    }

}
