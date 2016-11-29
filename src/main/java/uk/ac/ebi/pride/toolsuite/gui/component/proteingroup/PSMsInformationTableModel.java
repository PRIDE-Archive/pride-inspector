package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import uk.ac.ebi.jmzidml.model.mzidml.Modification;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptideSpectrumMatch;

/**
 * Model showing information of the PSMs in the peptides
 * 
 * @author julianu
 *
 */
public class PSMsInformationTableModel extends AbstractTableModel {
    /** this list holds the intermediate proteins */
    private List<IntermediatePeptideSpectrumMatch> psmsList;
    
    private Map<Integer, String> modificationStrings;
    
    
    /** formatter to show m/z values in PSM labels */
    final static DecimalFormat decimalFormatter;
    
    static {
        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(Locale.US);
        decimalFormatter = new DecimalFormat("0.####");
        decimalFormatter.setDecimalFormatSymbols(decimalSymbols);
    }
    
    protected enum Columns {
        INDEX,
        SEQUENCE,
        DELTA_MZ,
        CHARGE,
        PRECURSOR_MZ,
        MODIFICATIONS,
        LENGTH,
        ;
    }
    
    
    public PSMsInformationTableModel(Collection<?> peptides) {
        setPeptides(peptides);
    }
    
    
    public void setPeptides(Collection<?> peptides) {
        psmsList = new ArrayList<>();
        modificationStrings = new HashMap<>();
        
        if (peptides != null) {
            for (Object item : peptides) {
                if (item instanceof IntermediatePeptide) {
                    psmsList.addAll(((IntermediatePeptide) item).getAllPeptideSpectrumMatches());
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
            
        case SEQUENCE:
            return psmsList.get(rowIndex).getSequence();
            
        case DELTA_MZ:
            return decimalFormatter.format(psmsList.get(rowIndex).getDeltaMass());
            
        case CHARGE:
            return psmsList.get(rowIndex).getCharge();
            
        case PRECURSOR_MZ:
            return decimalFormatter.format(psmsList.get(rowIndex).getExperimentalMassToCharge());
            
        case MODIFICATIONS:
            if (!modificationStrings.containsKey(rowIndex)) {
                StringBuilder modSb = new StringBuilder();
                
                for (Modification mod :  psmsList.get(rowIndex).getModifications()) {
                    if (modSb.length() > 0) {
                        modSb.append(", ");
                    }
                    
                    modSb.append(mod.getLocation());
                    modSb.append('-');
                    
                    if (mod.getCvParam().size() > 0) {
                        modSb.append(mod.getCvParam().get(0).getName());
                    } else {
                        modSb.append(decimalFormatter.format(mod.getMonoisotopicMassDelta()));
                    }
                }
                
                modificationStrings.put(rowIndex, modSb.toString());
            }
            
            return modificationStrings.get(rowIndex);
            
        case LENGTH:
            return psmsList.get(rowIndex).getSequence().length();
        }
        
        return null;
    }


    @Override
    public int getRowCount() {
        return psmsList.size();
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
            
        case SEQUENCE:
            return "Sequence";
            
        case DELTA_MZ:
            return "Delta m/z";
            
        case CHARGE:
            return "Charge";
            
        case PRECURSOR_MZ:
            return "Precursor m/z";
            
        case MODIFICATIONS:
            return "Modifications";
            
        case LENGTH:
            return "Length";
        }
        
        return null;
    }
}