package uk.ac.ebi.pride.toolsuite.gui.component.mzgraph;

import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.MzGraph;
import uk.ac.ebi.pride.utilities.data.core.Parameter;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.component.metadata.CollapsiblePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * User: rwang
 * Date: 21-Apr-2010
 * Time: 10:17:44
 */
public class MzGraphPropertyPane extends JPanel implements PropertyChangeListener {

    private PropertyPaneModel dataModel;
    private JComboBox comboBox;

    public MzGraphPropertyPane() {
        setupMainPane();
        addComponents();
    }

    private void setupMainPane() {
        this.setLayout(new BorderLayout());
        // set the data model for this MzGraphPropertyPane
        this.setModel(new PropertyPaneModel());
    }

    private void addComponents() {
        // create combo box
        comboBox = new JComboBox();
        PropertyPaneModel.CategoryComboBoxModel comboBoxDataModel = dataModel.getCategoryComboBoxModel();
        comboBox.setModel(comboBoxDataModel);

        // create property pane
        PropertyContentPane propPane = new PropertyContentPane();
        comboBoxDataModel.addPropertyChangeListener(propPane);

        // add property pane as a combo box action listener
        comboBox.addActionListener(propPane);
        this.add(comboBox, BorderLayout.NORTH);
        this.add(propPane, BorderLayout.CENTER);
    }

    public void setModel(PropertyPaneModel paneModel) {
        PropertyPaneModel oldModel = dataModel;
        if (oldModel != null) {
            oldModel.removePropertyChangeListener(this);
        }
        dataModel = paneModel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (DataAccessController.MZGRAPH_TYPE.equals(evt.getPropertyName())) {
            dataModel.setMzGraph((MzGraph) evt.getNewValue());
        }
    }

    private class PropertyContentPane extends JPanel implements ActionListener, PropertyChangeListener {

        private JPanel contentPane = null;
        private JScrollPane scrollPane = null;

        private PropertyContentPane() {
            this.setupMainPane();
            this.addComponents();
        }

        private void setupMainPane() {
            this.setLayout(new BorderLayout());
        }

        private void addComponents() {
            contentPane = new JPanel();
            contentPane.setBackground(Color.white);
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            //contentPane.setLayout(new FlowLayout());
            updateContentPane();
            scrollPane = new JScrollPane(contentPane, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.add(scrollPane, BorderLayout.CENTER);
        }

        public void updateContentPane() {
            contentPane.removeAll();

            // get category
            String category = (String) comboBox.getSelectedItem();
            if (category == null && comboBox.getModel().getSize() > 0) {
                comboBox.setSelectedIndex(0);
                category = (String) comboBox.getSelectedItem();
            }

            if (category != null) {
                // get sub-categroy
                Collection<String> subCategories = dataModel.getSubCategoryTitles(category);

                for (String subCategory : subCategories) {
                    Collection<Collection<Parameter>> content = dataModel.getDataBySubCategory(category, subCategory);
                    for (Collection<Parameter> parameters : content) {
                        CollapsiblePane collapsePane = new CollapsiblePane(subCategory);
                        JTable table = new PropertyTable(parameters);
                        collapsePane.setContentComponent(table);
                        contentPane.add(collapsePane);
                    }
                }
            }
            this.revalidate();
            this.repaint();
        }

        public void etdUpdate() {
            Runnable eventDispatcher = new Runnable() {
                public void run() {
                    updateContentPane();
                }
            };
            EDTUtils.invokeLater(eventDispatcher);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            etdUpdate();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (PropertyPaneModel.NEW_PROP_CONTENT_PROP.equals(evt.getPropertyName())) {
                etdUpdate();
            }
        }
    }
}
