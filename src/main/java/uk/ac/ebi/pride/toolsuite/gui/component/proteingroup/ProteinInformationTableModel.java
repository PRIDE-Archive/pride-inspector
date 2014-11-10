package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.mpc.pia.core.intermediate.IntermediateProtein;

/**
 * Model for collapsed proteins shown in the information panel
 * 
 * @author julianu
 *
 */
public class ProteinInformationTableModel extends AbstractTableModel {
	/** this list holds the intermediate proteins */
	private List<IntermediateProtein> proteinList;
	
	public ProteinInformationTableModel(Collection<?> proteins) {
		proteinList = new ArrayList<IntermediateProtein>(proteins.size());
		
		Iterator<?> objectIterator = proteins.iterator();
		while (objectIterator.hasNext()) {
			Object item = objectIterator.next();
			
			if (item instanceof IntermediateProtein) {
				proteinList.add((IntermediateProtein)item);
			}
		}
	}
	
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return rowIndex+1;
			
		case 1:
			return proteinList.get(rowIndex).getAccession();
		}
		
		return null;
	}
	
	
	@Override
	public int getRowCount() {
		return proteinList.size();
	}
	
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	
	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "#";
		case 1:
			return "Accession";
		}
		return null;
	}
}