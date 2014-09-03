package uk.ac.ebi.pride.toolsuite.gui.url;

import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccessionPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generate hyperlink based a given protein accession.
 * <p/>
 * User: rwang
 * Date: 10-Sep-2010
 * Time: 15:41:00
 */
public class ProteinAccHyperLinkGenerator implements HyperLinkGenerator<ProteinAccession> {

    @Override
    public String generate(ProteinAccession protein) {
        String url = null;
        if (protein != null) {
            String mappedAccession = protein.getMappedAccession();

            if (mappedAccession != null) {
                // iterate over id patterns
                for (ProteinAccessionPattern p : ProteinAccessionPattern.values()) {
                    Pattern idPattern = p.getIdPattern();
                    Matcher m = idPattern.matcher(mappedAccession);
                    if (m.matches()) {
                        Object[] args = {mappedAccession};
                        url = p.getUrlPattern().format(args);
                        break;
                    }
                }
            }
        }
        return url;
    }
}
