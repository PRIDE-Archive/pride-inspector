package uk.ac.ebi.pride.toolsuite.gui.url;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.net.URL;

/**
 * HyperlinkListener to open a url.
 *
 * User: rwang
 * Date: 10-Oct-2010
 * Time: 11:00:10
 */
public class HyperLinkFollower implements HyperlinkListener {
    private static final Logger logger = LoggerFactory.getLogger(HyperLinkFollower.class);

    public void hyperlinkUpdate(HyperlinkEvent evt) {

        if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                URL url = evt.getURL();
                if (url != null) {
                    HttpUtilities.openURL(url.toString());
                }
            } catch (Exception e) {
                logger.error("Failed to open a url: {}", evt.getURL().toString());
            }
        }

    }
}
