package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.mpc.pia.core.intermediate.IntermediatePeptide;

/**
 * Model for collapsed peptides shown in the information panel
 * 
 * @author julianu
 *
 */
public class PeptideInformationTableModel extends AbstractTableModel {
	/** this list holds the intermediate proteins */
	private List<IntermediatePeptide> peptideList;
	
	public PeptideInformationTableModel(Collection<?> peptides) {
		peptideList = new ArrayList<IntermediatePeptide>(peptides.size());
		
		Iterator<?> objectIterator = peptides.iterator();
		while (objectIterator.hasNext()) {
			Object item = objectIterator.next();
			
			if (item instanceof IntermediatePeptide) {
				peptideList.add((IntermediatePeptide)item);
			}
		}
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return rowIndex+1;
			
		case 1:
			return peptideList.get(rowIndex).getSequence();
			
		case 2:
			return peptideList.get(rowIndex).getNumberOfPeptideSpectrumMatches();
		}
		
		return null;
	}
	
	
	@Override
	public int getRowCount() {
		return peptideList.size();
	}
	
	
	@Override
	public int getColumnCount() {
		return 3;
	}
	
	
	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "#";
		case 1:
			return "Sequence";
		case 2:
			return "#PSMs";
		}
		
		return null;
	}
}