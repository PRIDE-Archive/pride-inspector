package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * Regular expression pattern and url for PTM accessions.
 *
 * User: rwang
 * Date: 16-Sep-2010
 * Time: 10:14:11
 */
public enum PTMAccessionPattern {
    PSIMOD(Pattern.compile("MOD:\\d+"), new MessageFormat("http://www.ebi.ac.uk/ontology-lookup/?termId={0}")),
    UNIMOD(Pattern.compile("\\d+"), new MessageFormat("http://www.unimod.org/modifications_view.php?editid1={0}")),
    RESID(Pattern.compile("[RESID:]?AA\\d+"), new MessageFormat("http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-id+1g5_71c51oV+-e+[RESID:{0}]"));

    private Pattern idPattern;
    private MessageFormat urlPattern;

    private PTMAccessionPattern(Pattern idPattern, MessageFormat urlPattern) {
        this.idPattern = idPattern;
        this.urlPattern = urlPattern;
    }

    public Pattern getIdPattern() {
        return idPattern;
    }

    public MessageFormat getUrlPattern() {
        return urlPattern;
    }
}
