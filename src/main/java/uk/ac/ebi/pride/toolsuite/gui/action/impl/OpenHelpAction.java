package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;

import javax.help.CSH;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Open Help manuel.
 *
 * User: rwang
 * Date: 18-Aug-2010
 * Time: 11:41:13
 */
public class OpenHelpAction extends PrideAction {
    private String helpIndex;

    public OpenHelpAction(String name, Icon icon, String helpIndex) {
        super(name, icon);
        this.helpIndex = helpIndex;
    }

    public OpenHelpAction(String name, Icon icon, String helpIndex, int acceleratorKey) {
        super(name, icon);
        setAccelerator(acceleratorKey);
        this.helpIndex = helpIndex;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext)PrideInspector.getInstance().getDesktopContext();
        CSH.setHelpIDString(PrideInspector.getInstance().getMainComponent(), helpIndex);
        ActionListener listener = new CSH.DisplayHelpFromSource(context.getMainHelpBroker());
        listener.actionPerformed(e);
    }
}
