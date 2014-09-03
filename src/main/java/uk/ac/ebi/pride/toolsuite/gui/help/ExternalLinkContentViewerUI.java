package uk.ac.ebi.pride.toolsuite.gui.help;

import uk.ac.ebi.pride.toolsuite.gui.url.HttpUtilities;

import javax.help.JHelpContentViewer;
import javax.help.plaf.basic.BasicContentViewerUI;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.ComponentUI;
import java.net.URL;

/**
 * Enable opening hyperlinks in external browser
 *
 * @author rwang
 * @author ypriverol
 * @version $Id$
 */
public class ExternalLinkContentViewerUI extends BasicContentViewerUI {

    public ExternalLinkContentViewerUI(JHelpContentViewer b) {
        super(b);
    }

    public static ComponentUI createUI(JComponent x) {
        return new ExternalLinkContentViewerUI((JHelpContentViewer) x);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                URL u = e.getURL();
                if (u.getProtocol().equalsIgnoreCase("mailto") || u.getProtocol().equalsIgnoreCase("http") || u.getProtocol().equalsIgnoreCase("ftp")) {
                    HttpUtilities.openURL(u.toString());
                    return;
                }
            } catch (Throwable t) {
            }
        }
        super.hyperlinkUpdate(e);
    }
}
