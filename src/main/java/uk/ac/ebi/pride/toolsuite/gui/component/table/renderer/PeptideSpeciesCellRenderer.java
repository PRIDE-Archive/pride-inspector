package uk.ac.ebi.pride.toolsuite.gui.component.table.renderer;

import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpecies;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableRow;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ypriverol
 * Date: 10/15/13
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class PeptideSpeciesCellRenderer extends JLabel implements TableCellRenderer {

    public PeptideSpeciesCellRenderer() {
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        List<String> peptideSpeciesList = new ArrayList<>();
        if (value != null) {
            if (value instanceof PeptideSpecies) {
                PeptideSpecies peptideSpecies = (PeptideSpecies) value;
                for (PeptideTableRow peptideTableRow : peptideSpecies.getPeptideTableRowData()) {
                    peptideSpeciesList.add(peptideTableRow.getPeptideId().toString());
                }
            } else {
                Collection<PeptideSpecies> peptideSpeciesCollection = (Collection<PeptideSpecies>) value;
                for (PeptideSpecies peptideSpecies : peptideSpeciesCollection) {
                    for (PeptideTableRow peptideTableRow : peptideSpecies.getPeptideTableRowData()) {
                        peptideSpeciesList.add(peptideTableRow.getPeptideId().toString());
                    }
                }
            }
        }

        if (!peptideSpeciesList.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("<html>");
            for (String accession : peptideSpeciesList) {
                builder.append(accession);
                if (peptideSpeciesList.indexOf(accession) != peptideSpeciesList.size() - 1) builder.append(",");
            }
            builder.append("</html>");
            this.setText(builder.toString());
        } else {
            this.setText(null);
        }

        // set background
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            this.setBackground(table.getBackground());
        }
        // repaint the component
        this.revalidate();
        this.repaint();
        return this;
    }
}
