package uk.ac.ebi.pride.toolsuite.gui.component.mzgraph;

import uk.ac.ebi.pride.utilities.data.core.Chromatogram;
import uk.ac.ebi.pride.utilities.data.core.MzGraph;
import uk.ac.ebi.pride.utilities.data.core.Parameter;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.utilities.data.utils.CollectionUtils;
import uk.ac.ebi.pride.toolsuite.gui.utils.PropertyChangeHelper;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * PropertyPaneModel is a abstract class which is used as a data model for PropertyDisplayPane
 * User: rwang
 * Date: 21-Apr-2010
 * Time: 11:45:36
 */
public class PropertyPaneModel extends PropertyChangeHelper {

    public final static String NEW_PROP_CONTENT_PROP = "dataContent";

    /**
     * main data structure: Map<Category, <Sub-category, Collection<Collection<Parameter>>>>
     */
    private final Map<String, Map<String, Collection<Collection<Parameter>>>> dataContent;

    private CategoryComboBoxModel comboModel = null;

    /**
     * Constructor initializes the main data structure.
     */
    protected PropertyPaneModel() {
        this.dataContent = new LinkedHashMap<String, Map<String, Collection<Collection<Parameter>>>>();
    }

    /**
     * Get all categories
     *
     * @return Collection<String>   a collection of categories.
     */
    public Collection<String> getCategoryTitles() {
        Collection<String> results = new ArrayList<String>();
        if (dataContent != null) {
            results.addAll(dataContent.keySet());
        }
        return results;
    }

    /**
     * Get all sub-categories under specific category
     *
     * @param category category string name
     * @return Collection<String>   a collection of sub-category names.
     */
    public Collection<String> getSubCategoryTitles(String category) {
        Collection<String> results = new ArrayList<String>();
        if (dataContent != null && dataContent.get(category) != null) {
            results.addAll(dataContent.get(category).keySet());
        }
        return results;
    }

    /**
     * Add a collection of parameters into specific category and subcategory
     *
     * @param categoryStr    category string name
     * @param subCategoryStr sub-category string name
     * @param data           a collection of parameters
     */
    public void addData(String categoryStr, String subCategoryStr, Collection<Parameter> data) {
        Map<String, Collection<Collection<Parameter>>> subCategories = dataContent.get(categoryStr);
        if (subCategories == null) {
            subCategories = new HashMap<String, Collection<Collection<Parameter>>>();
            dataContent.put(categoryStr, subCategories);
        }
        Collection<Collection<Parameter>> subCategory = subCategories.get(subCategoryStr);
        if (subCategory == null) {
            subCategory = new ArrayList<Collection<Parameter>>();
            subCategories.put(subCategoryStr, subCategory);
        }
        subCategory.add(data);
    }

    /**
     * Append a collection of parameters to the last collection of parameters based on
     * both on category and sub-category.
     *
     * @param categoryStr    category string name
     * @param subCategoryStr sub-category string name
     * @param data           a collection of parameters to be attached
     */
    public void appendData(String categoryStr, String subCategoryStr, Collection<Parameter> data) {
        Map<String, Collection<Collection<Parameter>>> subCategories = dataContent.get(categoryStr);
        if (subCategories == null) {
            subCategories = new HashMap<String, Collection<Collection<Parameter>>>();
            dataContent.put(categoryStr, subCategories);
        }

        Collection<Collection<Parameter>> subCategory = subCategories.get(subCategoryStr);
        if (subCategory == null) {
            subCategory = new ArrayList<Collection<Parameter>>();
            subCategories.put(subCategoryStr, subCategory);
        }
        // attach data
        if (subCategory.size() > 0) {
            Collection<Parameter> params = CollectionUtils.getLastElement(subCategory);
            params.addAll(data);
        } else {
            subCategory.add(data);
        }
    }

    /**
     * Uses appendData(Collection) at the background
     *
     * @param categoryStr
     * @param subCategoryStr
     * @param param
     */
    public void appendData(String categoryStr, String subCategoryStr, Parameter param) {
        Collection<Parameter> params = new ArrayList<Parameter>();
        params.add(param);
        appendData(categoryStr, subCategoryStr, params);
    }

    /**
     * Get data content using both category and sub-category
     *
     * @param category    category string name
     * @param subCategory sub-category string name
     * @return Collection<Collection<Parameter>>    a collection of a group of parameters
     */
    public Collection<Collection<Parameter>> getDataBySubCategory(String category, String subCategory) {
        Collection<Collection<Parameter>> result = new ArrayList<Collection<Parameter>>();
        Map<String, Collection<Collection<Parameter>>> subCategories = dataContent.get(category);
        if (subCategories != null) {
            Collection<Collection<Parameter>> dataContent = subCategories.get(subCategory);
            if (dataContent != null) {
                for (Collection<Parameter> params : dataContent) {
                    Collection<Parameter> newParams = new ArrayList<Parameter>(params);
                    result.add(newParams);
                }
            }
        }
        return result;
    }

    public CategoryComboBoxModel getCategoryComboBoxModel() {
        if (comboModel == null) {
            comboModel = new CategoryComboBoxModel(dataContent.keySet());
            this.addPropertyChangeListener(comboModel);
        }
        return comboModel;
    }

    public void setMzGraph(MzGraph mzGraph) {
        dataContent.clear();
        if (mzGraph instanceof Spectrum) {
            PropertyPaneModelHelper.addSpectrum(this, (Spectrum) mzGraph);
        } else if (mzGraph instanceof Chromatogram) {
            PropertyPaneModelHelper.addChromatogram(this, (Chromatogram) mzGraph);
        }
        this.firePropertyChange(NEW_PROP_CONTENT_PROP, null, dataContent);
    }

    /**
     *
     */
    public class CategoryComboBoxModel extends PropertyChangeHelper
            implements ComboBoxModel, PropertyChangeListener {
        private Object selectedItem = null;
        private List<String> content = null;

        private CategoryComboBoxModel(Collection<String> content) {
            this.content = new ArrayList<String>(content);
        }

        @Override
        public void setSelectedItem(Object anItem) {
            this.selectedItem = anItem;
        }

        @Override
        public Object getSelectedItem() {
            return selectedItem;
        }

        @Override
        public int getSize() {
            return content.size();
        }

        @Override
        public Object getElementAt(int index) {
            return content.get(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String evtName = evt.getPropertyName();
            if (NEW_PROP_CONTENT_PROP.equals(evtName)) {
                // clear existing content
                content.clear();
                // add new content;
                if (dataContent.keySet().size() > 0) {
                    content.addAll(dataContent.keySet());
                    // update selectedItem
                    if (!content.contains(selectedItem)) {
                        selectedItem = content.get(0);
                    }
                }
                // data content
                this.firePropertyChange(NEW_PROP_CONTENT_PROP, null, dataContent);
            }
        }
    }
}
