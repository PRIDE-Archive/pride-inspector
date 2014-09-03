package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.ParamGroup;
import uk.ac.ebi.pride.utilities.data.core.Parameter;
import uk.ac.ebi.pride.utilities.data.core.UserParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Table model for storing both cv params and user params
 * User: rwang
 * Date: 24/07/2011
 * Time: 10:29
 */
public class ParamTableModel extends ListBasedListTableModel<Parameter> {

    /**
     * table column title
     */
    public enum TableHeader {
        ONTOLOGY("Ontology", "Short Name Of the Ontology"),
        ACCESSION("Accession", "Accession Defined By The Ontology"),
        Name("Name", "Name Defined By The Ontology"),
        VALUE("Value", "Value Of This Ontology Field");

        private final String header;
        private final String toolTip;

        private TableHeader(String header, String tooltip) {
            this.header = header;
            this.toolTip = tooltip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    private List<Parameter> parameters;

    public ParamTableModel(List<ParamGroup> paramGroups) {

        this.parameters = new ArrayList<Parameter>();

        for (ParamGroup paramGroup : paramGroups) {
            // cv parameters
            List<CvParam> cvs = paramGroup.getCvParams();
            parameters.addAll(cvs);
            // user parameters
            List<UserParam> up = paramGroup.getUserParams();
            parameters.addAll(up);
        }

        // add all the parameters
        for (Parameter parameter : parameters) {
            addData(parameter);
        }
    }

    /**
     * Constructor for adding a param group
     *
     * @param paramGroup given paramGroup
     */
    public ParamTableModel(ParamGroup paramGroup) {
        this.parameters = new ArrayList<Parameter>();

        // cv parameters
        List<CvParam> cvs = paramGroup.getCvParams();
        if (!cvs.isEmpty()) {
            parameters.addAll(cvs);
        }
        // user parameters
        List<UserParam> up = paramGroup.getUserParams();
        if (!up.isEmpty()) {
            parameters.addAll(up);
        }
        // add all the parameters
        for (Parameter parameter : parameters) {
            addData(parameter);
        }
    }

    /**
     * Constructor for adding a collection of params
     *
     * @param parameters a collection of params
     */
    public ParamTableModel(Collection<Parameter> parameters) {
        this.parameters = new ArrayList<Parameter>();
        parameters.addAll(parameters);
        // add all the parameters
        for (Parameter parameter : parameters) {
            addData(parameter);
        }
    }

    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(Parameter newData) {
        List<Object> data = new ArrayList<Object>();

        // row count
        int rowCnt = this.getRowCount();

        if (newData instanceof CvParam) {
            CvParam cvParam = (CvParam) newData;
            data.add(cvParam.getCvLookupID());
            data.add(cvParam.getAccession());
            data.add(cvParam.getName());
            data.add(cvParam.getValue());
        } else {
            UserParam userParam = (UserParam) newData;
            data.add(null);
            data.add(null);
            data.add(userParam.getName());
            data.add(userParam.getValue());
        }

        this.addRow(data);
        fireTableRowsInserted(rowCnt, rowCnt);
    }
}
