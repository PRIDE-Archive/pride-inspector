package uk.ac.ebi.pride.toolsuite.gui.component.table.filter;

import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.utilities.data.filter.DecoyAccessionFilter;

import javax.swing.*;
import java.util.Set;

/**
 * User: rwang
 * Date: 01/09/2011
 * Time: 12:19
 */
public class DecoyAccessionTableFilter extends RowFilter {

    /**
     * Decoy accession filter
     */
    private final DecoyAccessionFilter filter;

    /**
     * Index of the protein accession column
     */
    private final int accessionColumnIndex;

    /**
     * Constructor
     *
     * @param accessionColumnIndex index of the protein accession
     */
    public DecoyAccessionTableFilter(DecoyAccessionFilter filter, int accessionColumnIndex) {
        this.filter = filter;
        this.accessionColumnIndex = accessionColumnIndex;
    }

    @Override
    public boolean include(Entry entry) {
        String accession = getProteinAccession(entry, accessionColumnIndex);
        return filter.apply(accession);
    }

    private String getProteinAccession(Entry entry, int index) {
        Object proteinAccession = entry.getValue(index);
        if (proteinAccession instanceof ProteinAccession) {
            return ((ProteinAccession) proteinAccession).getAccession();
        } else {
            Set<ProteinAccession> proteinAccessions = (Set<ProteinAccession>) proteinAccession;
            if (!proteinAccessions.isEmpty()) {
                return proteinAccessions.iterator().next().getAccession();
            }
        }

        return null;
    }

    public DecoyAccessionFilter getFilter() {
        return filter;
    }

    public int getAccessionColumnIndex() {
        return accessionColumnIndex;
    }
}
