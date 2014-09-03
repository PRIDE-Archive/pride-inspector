package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.LoginRecord;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.MyProjectSummaryDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.PrideLoginDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Open reviewer download panel.
 *
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 09:44:36
 */
public class OpenMyProjectAction extends PrideAction {

    public OpenMyProjectAction(String name, Icon icon) {
        super(name, icon);
        this.setAccelerator(java.awt.event.KeyEvent.VK_R, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();

        LoginRecord loginRecord = context.getLoginRecord();
        if (loginRecord == null) {
            openLoginDialog();
        } else {
            openProjectSummaryDialog();
        }
    }

    private void openProjectSummaryDialog() {
        MyProjectSummaryDialog myProjectSummaryDialog = new MyProjectSummaryDialog(PrideInspector.getInstance().getMainComponent());
        myProjectSummaryDialog.setVisible(true);
    }

    private void openLoginDialog() {
        PrideLoginDialog prideLoginDialog = new PrideLoginDialog(PrideInspector.getInstance().getMainComponent());
        prideLoginDialog.setVisible(true);
    }
}
