package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.url.HyperLinkFollower;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Action to leave feedback, this will call the local installed email client.
 *
 * User: rwang
 * Date: 10-Oct-2010
 * Time: 11:08:34
 */
public class FeedbackAction extends PrideAction {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackAction.class);

    public FeedbackAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        HyperLinkFollower follower = new HyperLinkFollower();
        try {
            String prideHelpDeskpEmail = context.getProperty("pride.helpdesk.email");
            follower.hyperlinkUpdate(new HyperlinkEvent(this, HyperlinkEvent.EventType.ACTIVATED, new URL("mailto:" + prideHelpDeskpEmail)));
        } catch (MalformedURLException e1) {
            logger.error("Fail to create URL for leaving feedback", e1);
        }
    }
}
