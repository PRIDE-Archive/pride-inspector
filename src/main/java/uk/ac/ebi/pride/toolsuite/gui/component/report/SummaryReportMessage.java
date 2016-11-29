package uk.ac.ebi.pride.toolsuite.gui.component.report;

/**
 * Report message contains message status
 *
 * User: rwang
 * Date: 25/05/11
 * Time: 12:28
 */
public class SummaryReportMessage implements ReportMessage{
    public enum Type {SUCCESS, WARNING, ERROR, INFO}

    private final Type type;
    private final String message;
    private final String description;

    public SummaryReportMessage(Type type, String message, String description) {
        this.type = type;
        this.message = message;
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}