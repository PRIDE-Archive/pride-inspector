package uk.ac.ebi.pride.toolsuite.gui.component.db;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.DatabaseSearchTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.toolsuite.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.toolsuite.gui.search.Criteria;
import uk.ac.ebi.pride.toolsuite.gui.search.SearchEntry;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.SearchDatabaseTask;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * DatabaseSearchPane is the main panel contains a search box and a search result table
 * <p/>
 * All the search actions for the PRIDE public instance is done through this panel
 */
public class DatabaseSearchPane extends DataAccessControllerPane<Void, Void> {
    private static final String PANE_TITLE = "Search Database";
    private static final Color BACKGROUND_COLOUR = Color.white;

    private JLabel searchLabel;
    private JComboBox categoryComboBox;
    private JComboBox criteriaComboBox;
    private JTextField searchTextField;
    private JButton searchButton;
    private JCheckBox searchResultCheckBox;
    private JButton resetSearchButton;
    private JTable searchResultTable;
    private JPanel resultSummaryPanel;
    private JButton closeButton;
    private JLabel searchResultLabel;
    private PrideInspectorContext context;

    private int resultCount = 0;

    public DatabaseSearchPane(JComponent parentComp) {
        super(null, parentComp);
        // enable annotation
        AnnotationProcessor.process(this);
    }

    protected void setupMainPane() {
        this.setBackground(BACKGROUND_COLOUR);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.gray));
        this.setTitle(PANE_TITLE);

        // set the final icon
        this.setIcon(GUIUtilities.loadIcon(appContext.getProperty("database.search.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(appContext.getProperty("database.search.loading.icon.small")));

        context = (PrideInspectorContext)uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();

        popupOffLineMessage();
    }

    protected void popupOffLineMessage(){
        JOptionPane.showMessageDialog(null,context.getProperty("pride.inspector.pride.out.message"),"PRIDE Instance Outdated",JOptionPane.OK_OPTION);
    }



    protected void addComponents() {
        JPanel container = new JPanel();
        JPanel helpButtonPanel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        searchLabel = new JLabel();
        categoryComboBox = new JComboBox(new FieldComboBoxModel());
        criteriaComboBox = new JComboBox(Criteria.toArray());
        searchTextField = new JTextField();
        searchButton = new JButton();
        searchResultCheckBox = new JCheckBox();
        resetSearchButton = GUIUtilities.createLabelLikeButton(null, "Reset");
        JPanel panel4 = new JPanel();
        JScrollPane scrollPane1 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        searchResultTable = TableFactory.createDatabaseSearchTable();
        resultSummaryPanel = new JPanel();
        searchResultLabel = new JLabel();
        closeButton = new JButton();

        // help button
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);

        //======== container panel ==========
        container.setLayout(new BorderLayout());
        container.setOpaque(false);

        //======== help button panel ========
        helpButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        helpButtonPanel.setOpaque(false);

        //-------- help button -----------
        helpButton.setToolTipText("Help on " + PANE_TITLE);
        CSH.setHelpIDString(helpButton, "help.pridedb");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        helpButtonPanel.add(helpButton);
        helpButtonPanel.setPreferredSize(new Dimension(200, 30));
        container.add(helpButtonPanel, BorderLayout.NORTH);
        add(helpButtonPanel, BorderLayout.NORTH);

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setOpaque(false);
            panel1.setLayout(new FlowLayout());

            //======== panel2 ========
            {
                panel2.setOpaque(false);

                //======== panel3 ========
                {
                    panel3.setBorder(new LineBorder(Color.black));
                    panel3.setBackground(new Color(214, 241, 249));

                    //---- searchLabel ----
                    searchLabel.setText("Search for:");

                    //---- categoryComboBox ----
                    categoryComboBox.setOpaque(false);

                    //---- criteriaComboBox ----
                    criteriaComboBox.setSelectedIndex(0);
                    criteriaComboBox.setEditable(false);
                    criteriaComboBox.setOpaque(false);

                    //---- searchTextField ----
                    searchTextField.addKeyListener(new SearchKeyListener());

                    //---- searchButton ----
                    searchButton.setText("Search");
                    searchButton.setOpaque(false);
                    searchButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            search();
                        }
                    });

                    //---- searchResultCheckBox ----
                    searchResultCheckBox.setText("Search within results");
                    searchResultCheckBox.setOpaque(false);
                    searchResultCheckBox.setEnabled(false);

                    //---- resetSearchButton ----
                    resetSearchButton.setForeground(Color.blue);
                    resetSearchButton.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            searchResultCheckBox.setSelected(false);
                            searchTextField.setText(null);
                            search();
                        }
                    });

                    GroupLayout panel3Layout = new GroupLayout(panel3);
                    panel3.setLayout(panel3Layout);
                    panel3Layout.setHorizontalGroup(
                            panel3Layout.createParallelGroup()
                                    .addGroup(panel3Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(searchLabel)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(categoryComboBox, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
                                            .addGap(12, 12, 12)
                                            .addComponent(criteriaComboBox, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(panel3Layout.createParallelGroup()
                                                    .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                                            .addComponent(searchResultCheckBox, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
                                                            .addGap(10, 10, 10)
                                                            .addComponent(resetSearchButton))
                                                    .addGroup(panel3Layout.createSequentialGroup()
                                                            .addComponent(searchTextField, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(searchButton)))
                                            .addContainerGap())
                    );
                    panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(searchButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(searchTextField)
                                                    .addComponent(categoryComboBox)
                                                    .addComponent(searchLabel)
                                                    .addComponent(criteriaComboBox))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(panel3Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(resetSearchButton)
                                                    .addComponent(searchResultCheckBox))
                                            .addGap(18, 18, 18))
                    );
                }

                GroupLayout panel2Layout = new GroupLayout(panel2);
                panel2.setLayout(panel2Layout);
                panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                                .addGroup(panel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                                .addGroup(panel2Layout.createSequentialGroup()
                                        .addContainerGap(12, Short.MAX_VALUE)
                                        .addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                );
            }
            panel1.add(panel2);
        }
        container.add(panel1, BorderLayout.NORTH);

        //======== panel4 ========
        {
            panel4.setBackground(BACKGROUND_COLOUR);

            //-------- searchResultLabel -----------
            searchResultLabel.setFont(searchResultLabel.getFont().deriveFont(Font.BOLD));

            //======== scrollPane1 ========
            {
                scrollPane1.setOpaque(false);
                scrollPane1.setBorder(BorderFactory.createLineBorder(Color.black));

                //---- searchResultTable ----
                // set view experiment cell renderer
                searchResultTable.setBorder(null);
                searchResultTable.setFillsViewportHeight(true);
                scrollPane1.setViewportView(searchResultTable);
            }

            //======== resultSummaryPanel ========
            {
                resultSummaryPanel.setBackground(BACKGROUND_COLOUR);
                resultSummaryPanel.setOpaque(false);
                resultSummaryPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                resultSummaryPanel.add(searchResultLabel);
            }

            //---- closeButton ----
            closeButton.setText("Close");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventBus.publish(new DatabaseSearchEvent<Void>(null, DatabaseSearchEvent.Status.HIDE));
                }
            });

            GroupLayout panel4Layout = new GroupLayout(panel4);
            panel4.setLayout(panel4Layout);
            panel4Layout.setHorizontalGroup(
                    panel4Layout.createParallelGroup()
                            .addGroup(panel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(panel4Layout.createParallelGroup()
                                            .addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                                    .addComponent(closeButton, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                                    .addGap(20, 20, 20))
                                            .addGroup(GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                                    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                            .addComponent(resultSummaryPanel, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                                                            .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
                                                    .addContainerGap())))
            );
            panel4Layout.setVerticalGroup(
                    panel4Layout.createParallelGroup()
                            .addGroup(panel4Layout.createSequentialGroup()
                                    .addComponent(resultSummaryPanel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(panel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(closeButton))
                                    .addGap(13, 13, 13))
            );
        }
        container.add(panel4, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }


    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchResultEvent(DatabaseSearchEvent evt) {
        switch (evt.getStatus()) {
            case START:
                Icon icon = GUIUtilities.loadIcon(appContext.getProperty("loading.small.icon"));
                searchResultLabel.setIcon(icon);
                break;
            case COMPLETE:
                searchResultTable.setRowSorter(new NumberTableRowSorter(searchResultTable.getModel()));
                searchResultLabel.setIcon(null);
                break;
            case RESULT:
                // enable search result check box
                if (!searchResultCheckBox.isEnabled()) {
                    searchResultCheckBox.setEnabled(true);
                }

                // update search result label
                java.util.List<java.util.List<String>> results = (java.util.List<java.util.List<String>>) evt.getResult();
                resultCount += results.size();
                searchResultLabel.setText(resultCount + " results found");
                break;
        }
    }

    private void search() {
        // reset the search result count
        resultCount = 0;

        // check the status of the search result check box
        boolean searchWithinResults = searchResultCheckBox.isSelected();

        // clear the content in search result table
        DatabaseSearchTableModel tableModel = (DatabaseSearchTableModel) searchResultTable.getModel();
        // get the existing content of the table if to search within the results
        java.util.List<java.util.List<String>> contents = null;
        java.util.List<String> headers = null;
        if (searchWithinResults) {
            contents = tableModel.getAllContent();
            headers = tableModel.getAllHeaders();
        }
        tableModel.removeAllRows();

        // get search entry
        String field = categoryComboBox.getSelectedItem().toString();
        String criteria = criteriaComboBox.getSelectedItem().toString();
        Criteria c = Criteria.getCriteria(criteria);
        String term = searchTextField.getText().trim();
        SearchEntry searchEntry = new SearchEntry(field, c, term);

        SearchDatabaseTask task;
        if (searchWithinResults) {
            // search within the existing results
            task = new SearchDatabaseTask(searchEntry, headers, contents);
        } else {
            task = new SearchDatabaseTask(searchEntry);
        }
        TaskUtil.startBackgroundTask(task);
    }


    /**
     * Key listener to trigger a search action
     */
    private class SearchKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                search();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}

