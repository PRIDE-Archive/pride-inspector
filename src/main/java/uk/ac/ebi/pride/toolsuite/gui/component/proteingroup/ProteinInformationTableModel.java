package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateProtein;

/**
 * Model showing information of the proteins
 * 
 * @author julianu
 *
 */
public class ProteinInformationTableModel extends AbstractTableModel {
    /** this list holds the intermediate proteins */
    private List<IntermediateProtein> proteinList;
    
    /** mapping from list index to number of PSMs */
    private Map<Integer, Integer> nrPSMs;
    
    /** mapping from list index to number of peptides */
    private Map<Integer, Integer> nrPeptides;
    
    protected enum Columns {
        INDEX,
        ACCESSION,
        PSMS,
        PEPTIDES,
        ;
    }

    public ProteinInformationTableModel(Collection<?> proteins) {
        setProteins(proteins);
    }
    
    
    /**
     * set the current proteins
     * @param proteins
     */
    public void setProteins(Collection<?> proteins) {
        proteinList = new ArrayList<>();
        nrPSMs = new HashMap<>();
        nrPeptides = new HashMap<>();
        
        if (proteins != null) {
            for (Object item : proteins) {
                if (item instanceof IntermediateProtein) {
                    proteinList.add((IntermediateProtein) item);
                }
            }
        }
        
        fireTableDataChanged();
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex >= Columns.values().length) {
            return null;
        }
        
        switch (Columns.values()[columnIndex]) {
        case INDEX:
            return rowIndex+1;
            
        case ACCESSION:
            return proteinList.get(rowIndex).getAccession();
            
        case PSMS:
            if (nrPSMs.containsKey(rowIndex)) {
                return nrPSMs.get(rowIndex);
            } else {
                int nr = 0;
                for (IntermediatePeptide peptide : proteinList.get(rowIndex).getGroup().getAllPeptides()) {
                    nr += peptide.getNumberOfPeptideSpectrumMatches();
                }
                nrPSMs.put(rowIndex, nr);
                return nr;
            }
            
        case PEPTIDES:
            if (!nrPeptides.containsKey(rowIndex)) {
                nrPeptides.put(rowIndex, proteinList.get(rowIndex).getGroup().getAllPeptides().size());
            }
            return nrPeptides.get(rowIndex);
        }
        
        return null;
    }


    @Override
    public int getRowCount() {
        return proteinList.size();
    }


    @Override
    public int getColumnCount() {
        return Columns.values().length;
    }


    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex >= Columns.values().length) {
            return null;
        }
        
        switch (Columns.values()[columnIndex]) {
        case INDEX:
            return "#";
            
        case ACCESSION:
            return "Accession";
            
        case PSMS:
            return "#PSMs";
            
        case PEPTIDES:
            return "#Peptides";
        }
        return null;
    }
}