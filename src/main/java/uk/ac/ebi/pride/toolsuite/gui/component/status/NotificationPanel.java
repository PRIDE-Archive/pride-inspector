package uk.ac.ebi.pride.toolsuite.gui.component.status;

import net.java.balloontip.utils.ToolTipUtils;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.balloontip.TaskBalloon;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * NotificationPanel is to display an error icon when an exception has been caught.
 *
 * User: rwang
 * Date: 16-Nov-2010
 * Time: 13:29:54
 */
public class NotificationPanel extends StatusBarPanel {

    /**
     * Throwable dialog
     */
    private JDialog throwableMessageBoard;
    /**
     * Error message label
     */
    private JLabel errorLabel;
    /**
     * Reference to Pride inspector context
     */
    private PrideInspectorContext context;

    public NotificationPanel() {
        super(0, true);
        this.setLayout(new BorderLayout());

        // Throwable message board
        throwableMessageBoard = new NotificationDialog(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent());
        throwableMessageBoard.setVisible(false);

        // get pride inspector context
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();

        Icon icon = GUIUtilities.loadIcon(context.getProperty("no.exception.notify.small.icon"));
        errorLabel = new JLabel(icon);
        errorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                throwableMessageBoard.setVisible(true);
            }
        });
        this.add(errorLabel, BorderLayout.CENTER);

        // add itself as a listener to task manager
        ThrowableHandler throwableHandler = context.getThrowableHandler();
        throwableHandler.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String eventName = evt.getPropertyName();
        if (ThrowableHandler.ADD_THROWABLE_PROP.equals(eventName)) {
            Icon icon = GUIUtilities.loadIcon(context.getProperty("exception.notify.small.icon"));
            errorLabel.setIcon(icon);
        } else if (ThrowableHandler.REMOVE_THROWABLE_PROP.equals(eventName)) {
            List<ThrowableEntry> throwables = (List<ThrowableEntry>)evt.getNewValue();
            if (throwables.isEmpty()) {
                Icon icon = GUIUtilities.loadIcon(context.getProperty("no.exception.notify.small.icon"));
                errorLabel.setIcon(icon);
            }
        }

    }
}
