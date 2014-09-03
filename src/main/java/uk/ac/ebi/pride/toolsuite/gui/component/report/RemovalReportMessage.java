package uk.ac.ebi.pride.toolsuite.gui.component.report;

import java.util.regex.Pattern;

/**
 * Report message to remove messages from summary report viewer
 *
 * User: rwang
 * Date: 15/09/2011
 * Time: 20:28
 */
public class RemovalReportMessage implements ReportMessage{
    private final Pattern pattern;

    public RemovalReportMessage(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
