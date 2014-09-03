package uk.ac.ebi.pride.toolsuite.gui.url;

import uk.ac.ebi.pride.toolsuite.gui.utils.PTMAccessionPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generate hyperlink for ptm accessions.
 * <p/>
 * User: rwang
 * Date: 10-Sep-2010
 * Time: 15:40:41
 */
public class PTMHyperLinkGenerator implements HyperLinkGenerator<String> {

    @Override
    public String generate(String value) {
        String url = null;
        if (value != null) {
            // iterate over id patterns
            for (PTMAccessionPattern p : PTMAccessionPattern.values()) {
                Pattern idPattern = p.getIdPattern();
                Matcher m = idPattern.matcher(value);
                if (m.matches()) {
                    Object[] args = {value};
                    url = p.getUrlPattern().format(args);
                    break;
                }
            }
        }
        return url;
    }
}
