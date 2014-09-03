package uk.ac.ebi.pride.toolsuite.gui.action;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;

/**
 * User: rwang
 * Date: 09-Feb-2010
 * Time: 15:57:47
 */
public abstract class PrideAction extends AbstractAction implements MenuListener {

    private int keyCode;
    private int keyMod;
    private boolean hasAccelerator = false;
    private boolean isCheckBoxItem = false;
    private boolean isRadioItem = false;

    public PrideAction() {
        super();
    }

    public PrideAction(String name) {
        super(name);
    }

    public PrideAction(String name, javax.swing.Icon icon) {
        super(name, icon);
    }

    @Override
    public abstract void actionPerformed(ActionEvent e);

    @Override
    public void menuSelected(MenuEvent e) {
    }

    @Override
    public void menuDeselected(MenuEvent e) {
    }

    @Override
    public void menuCanceled(MenuEvent e) {
    }

    public void setAccelerator(int keyCode) {
        setAccelerator(keyCode, 0);
    }

    public void setAccelerator(int keyCode, int keyMod) {
        hasAccelerator = true;
        this.keyCode = keyCode;
        this.keyMod = keyMod;
    }

    public boolean isAccelerated() {
        return hasAccelerator;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyMod() {
        return keyMod;
    }

    public void setKeyMod(int keyMod) {
        this.keyMod = keyMod;
    }

    public boolean isCheckBoxItem() {
        return isCheckBoxItem;
    }

    public void setCheckBoxItem(boolean checkBoxItem) {
        isCheckBoxItem = checkBoxItem;
    }

    public boolean isRadioItem() {
        return isRadioItem;
    }

    public void setRadioItem(boolean radioItem) {
        isRadioItem = radioItem;
    }
}
