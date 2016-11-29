package uk.ac.ebi.pride.toolsuite.gui.component.utils;

import javax.swing.*;

/**
 * <p>Interface used for manage the icon in the panes added to the tabpane</p>
 *
 * @author Antonio Fabregat
 * Date: 27-ago-2010
 * Time: 14:04:58
 */
public interface Iconable {

    void setIcon(Icon icon);
    Icon getLoadingIcon();
    Icon getIcon();

}
