package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.url.HttpUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action to open a url
 *
 * User: rwang
 * Date: 02-Nov-2010
 * Time: 23:41:59
 */
public class OpenUrlAction extends PrideAction{
    private String url;

    public OpenUrlAction(String name, Icon icon, String url) {
        super(name, icon);
        this.url = url;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HttpUtilities.openURL(url);
    }
}
