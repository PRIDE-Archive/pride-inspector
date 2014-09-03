package uk.ac.ebi.pride.toolsuite.gui.url;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Generate a DOI url
 *
 * User: rwang
 * Date: 24/07/2011
 * Time: 10:11
 */
public class DOIHyperLinkGenerator implements HyperLinkGenerator {
    private final static Logger logger = LoggerFactory.getLogger(DOIHyperLinkGenerator.class);

    private String prefix;

    public DOIHyperLinkGenerator(String urlPrefix) {
        this.prefix = urlPrefix;
    }

    public String generate(Object value) {
        String url = null;

        if (value != null && !"".equals(value.toString().trim())) {
            String v = String.valueOf(value).toLowerCase().replace("doi:", "");
            try {
                url = prefix + URLEncoder.encode(v, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                String errMsg = "Failed to generate a valid DOI URL";
                logger.error(errMsg);
                GUIUtilities.warn(Desktop.getInstance().getMainComponent(), errMsg, "URL Error");
            }
        }

        return url;
    }
}
