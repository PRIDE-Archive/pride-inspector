package uk.ac.ebi.pride.toolsuite.gui.component.startup;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.report.SummaryReportViewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DataSourceBrowser is an aggregate component, it contains three components:
 * 1. DataSourceViewer, which keeps tracking all the opening experiments
 * 2. SummaryReportViewer, which displays the summary report for selected experiment
 * 3. LaunchMenuViewer, which includes a list of buttons to launch start different features, e.g. database search.
 * <p/>
 * User: rwang
 * Date: 19-Aug-2010
 * Time: 11:28:07
 */
public class LeftControlPane extends JPanel {
    /**
     * reference to pride inspector context
     */
    private PrideInspectorContext context;

    public LeftControlPane() {
        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
    }

    private void addComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        splitPane2 = new JSplitPane();
        panel2 = new JPanel();
        dataSourceLabel = new JLabel();
        dataSourcePanel = new JPanel();
        panel3 = new JPanel();
        summaryLabel = new JLabel();
        summaryPanel = new JPanel();
        launchMenuPanel = new JPanel();

        //======== this ========

        //======== splitPane2 ========
        {
            splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane2.setBorder(BorderFactory.createEmptyBorder());
            splitPane2.setResizeWeight(0.5);
            splitPane2.setContinuousLayout(true);
            splitPane2.setDividerSize(5);

            //======== panel2 ========
            {

                //---- dataSourceLabel ----
                dataSourceLabel.setText("Experiments");
                dataSourceLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

                //======== dataSourcePanel ========
                {
//                    dataSourcePanel.setBackground(Color.white);
                    //dataSourcePanel.setBorder(new DropShadowBorder(Color.darkGray, 5));
//                    dataSourcePanel.setForeground(Color.lightGray);
                    dataSourcePanel.setLayout(new BorderLayout());
                }

                GroupLayout panel2Layout = new GroupLayout(panel2);
                panel2.setLayout(panel2Layout);
                panel2Layout.setHorizontalGroup(
                    panel2Layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(dataSourcePanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                                        .addComponent(dataSourceLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))
                                .addContainerGap())
                );
                panel2Layout.setVerticalGroup(
                    panel2Layout.createParallelGroup()
                        .addGroup(panel2Layout.createSequentialGroup()
                            .addComponent(dataSourceLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(dataSourcePanel, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                            .addContainerGap())
                );
            }
            splitPane2.setTopComponent(panel2);

            //======== panel3 ========
            {

                //---- summaryLabel ----
                summaryLabel.setText("Experiment to Summary");
                summaryLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

                //======== summaryPanel ========
                {
                    summaryPanel.setBackground(Color.white);
//                    summaryPanel.setBorder(new LineBorder(Color.black));
                    summaryPanel.setForeground(Color.lightGray);
                    summaryPanel.setLayout(new BorderLayout());
                }

                GroupLayout panel3Layout = new GroupLayout(panel3);
                panel3.setLayout(panel3Layout);
                panel3Layout.setHorizontalGroup(
                    panel3Layout.createParallelGroup()
                        .addGroup(panel3Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(panel3Layout.createParallelGroup()
                                .addComponent(summaryPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                                .addComponent(summaryLabel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))
                            .addContainerGap())
                );
                panel3Layout.setVerticalGroup(
                    panel3Layout.createParallelGroup()
                        .addGroup(panel3Layout.createSequentialGroup()
                            .addComponent(summaryLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(summaryPanel, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                            .addContainerGap())
                );
            }
            splitPane2.setBottomComponent(panel3);
        }

        //======== launchMenuPanel ========
//        {
//            launchMenuPanel.setBackground(Color.white);
//            //launchMenuPanel.setBorder(new DropShadowBorder());
//            launchMenuPanel.setForeground(Color.lightGray);
//            launchMenuPanel.setLayout(new BorderLayout());
//        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(splitPane2, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap())
//                    .addComponent(launchMenuPanel, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
//                    .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
//                    .addComponent(launchMenuPanel, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(splitPane2, GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        addSubComponents();
    }

    private void addSubComponents() {
        // data source viewer
        DataSourceViewer dataSourceViewer = new DataSourceViewer();
        JScrollPane dataSourceScrollPane = new JScrollPane(dataSourceViewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dataSourcePanel.add(dataSourceScrollPane, BorderLayout.CENTER);

        // get display related details
        Icon dataSourceViewerIcon = GUIUtilities.loadIcon(context.getProperty("data.source.viewer.small.icon"));
        String dataSourceTitle = context.getProperty("data.source.viewer.title");
        String dataSourceToolTip = context.getProperty("data.source.viewer.tooltip");
        dataSourceLabel.setIcon(dataSourceViewerIcon);
        dataSourceLabel.setText(dataSourceTitle);
        dataSourceLabel.setToolTipText(dataSourceToolTip);

        // experiment tree viewer
        summaryReportViewer = new SummaryReportViewer();
        summaryPanel.add(summaryReportViewer, BorderLayout.CENTER);

        // get display related details
        Icon sumReportViewerIcon = GUIUtilities.loadIcon(context.getProperty("summary.report.viewer.small.icon"));
        String sumReportTitle = context.getProperty("summary.report.viewer.title");
        String sumReportToolTip = context.getProperty("summary.report.viewer.tooltip");
        summaryLabel.setIcon(sumReportViewerIcon);
        summaryLabel.setText(sumReportTitle);
        summaryLabel.setToolTipText(sumReportToolTip);

//        // launch menu viewer
//        LaunchMenuViewer launchMenuViewer = new LaunchMenuViewer();
//        launchMenuPanel.add(launchMenuViewer, BorderLayout.CENTER);

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JSplitPane splitPane2;
    private JPanel panel2;
    private JLabel dataSourceLabel;
    private JPanel dataSourcePanel;
    private JPanel panel3;
    private JLabel summaryLabel;
    private JPanel summaryPanel;
    private JPanel launchMenuPanel;
    private SummaryReportViewer summaryReportViewer;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void setSummaryReportViewer(SummaryReportViewer summaryReportViewer) {
        this.summaryReportViewer = summaryReportViewer;
    }

    public SummaryReportViewer getSummaryReportViewer() {
        return summaryReportViewer;
    }
}
