package uk.ac.ebi.pride.toolsuite.gui.component.exception;

import uk.ac.ebi.pride.toolsuite.gui.component.message.Message;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;

/**
 * ThrowableEntry stores a entry of Throwable with its title.
 *
 * User: rwang
 * Date: 16-Nov-2010
 * Time: 11:34:05
 */
public class ThrowableEntry extends Message {

    private String title;
    private Throwable err;

    public ThrowableEntry(MessageType type, String title, Throwable err) {
        super(type);
        this.title = title;
        this.err = err;
    }

    @Override
    public String getMessage() {
        return "<html>" + "<h3>" + title + "</h3>" + "<p>" + err.toString() + "</p>" + "</html>";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Throwable getErr() {
        return err;
    }

    public void setErr(Throwable err) {
        this.err = err;
    }
}
