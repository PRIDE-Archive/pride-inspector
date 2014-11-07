package uk.ac.ebi.pride.toolsuite.gui.url;

import uk.ac.ebi.pride.utilities.util.RegExUtilities;

/**
 * Generate a url for email
 *
 * User: rwang
 * Date: 24/07/2011
 * Time: 12:33
 */
public class EmailHyperLinkGenerator implements HyperLinkGenerator {
    private static final String PREFIX = "mailto:";

    @Override
    public String generate(Object value) {
        String url = null;

        String email = value.toString();
        if (RegExUtilities.isValidEmail(email)) {
            url = PREFIX + email;
        }

        return url;
    }
}
