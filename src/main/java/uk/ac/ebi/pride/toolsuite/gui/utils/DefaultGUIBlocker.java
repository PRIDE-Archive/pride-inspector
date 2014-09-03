package uk.ac.ebi.pride.toolsuite.gui.utils;

import uk.ac.ebi.pride.toolsuite.gui.task.Task;

import javax.swing.*;
import java.awt.*;

/**
 * User: rwang
 * Date: 15-Feb-2010
 * Time: 11:09:51
 */
public class DefaultGUIBlocker extends GUIBlocker<Task, Object> {

    public DefaultGUIBlocker(Task src, Scope s, Object tar) {
        super(src, s, tar);
    }

    @Override
    public void block() {
        switch (this.getScope()) {
            case ACTION:
                setActionBlocked(true);
                break;
            case COMPONENT:
                setComponentBlocked(true);
                break;
            case DESKTOP:
                setDesktopBlocked(true);
                break;
        }
    }

    @Override
    public void unblock() {
        switch (this.getScope()) {
            case ACTION:
                setActionBlocked(false);
                break;
            case COMPONENT:
                setComponentBlocked(false);
                break;
            case DESKTOP:
                setDesktopBlocked(false);
                break;
        }
    }

    private void setDesktopBlocked(boolean b) {
        //ToDo: to be implemented
    }

    private void setComponentBlocked(boolean b) {
        Component component = (Component) this.getTarget();
        component.setEnabled(!b);
    }

    private void setActionBlocked(boolean b) {
        Action action = (Action) this.getTarget();
        action.setEnabled(!b);
    }


}
