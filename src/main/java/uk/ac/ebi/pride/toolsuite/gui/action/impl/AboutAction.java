package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.AboutDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Show About panel.
 *
 * @author rwang
 * @author ypriverol
 *
 * Date: 18-Aug-2010
 * Time: 14:28:10
 */
public class AboutAction extends PrideAction {

    public AboutAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AboutDialog(PrideInspector.getInstance().getMainComponent());
    }
}
