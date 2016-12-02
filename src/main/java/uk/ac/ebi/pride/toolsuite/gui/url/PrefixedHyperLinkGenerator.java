package uk.ac.ebi.pride.toolsuite.gui.url;

/**
 * Generate a hyperlink using a given url prefx
 *
 * e.g. for pubmed, http://www.ncbi.nlm.nih.gov/pubmed/
 *
 * User: ypriverol
 * Date: 24/07/2011
 * Time: 09:52
 */
public class PrefixedHyperLinkGenerator implements HyperLinkGenerator {
    private String prefix;

    public PrefixedHyperLinkGenerator(String urlPrefix) {
        this.prefix = urlPrefix;
    }

    @Override
    public String generate(Object value) {
        String url = null;
        if (value != null && !"".equals(value.toString().trim())) {
            url = prefix + value;
        }
        return url;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
