package uk.ac.ebi.pride.toolsuite.gui.url;

/**
 * Interface for generate a valid url based on the given value.
 *
 * User: rwang
 * Date: 10-Sep-2010
 * Time: 15:14:46
 */
public interface HyperLinkGenerator<K> {
    /**
     * generate a url
     *
     * @param value input value
     * @return String   a url string
     */
    String generate(K value);
}
