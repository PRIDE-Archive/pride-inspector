package uk.ac.ebi.pride.toolsuite.gui.component.status;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableHandler;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * NotificationDialog to display all the exceptions.
 *
 * User: rwang
 * Date: 16-Nov-2010
 * Time: 13:33:09
 */
public class NotificationDialog extends JDialog implements PropertyChangeListener, ActionListener {
    private static final String NOTIFICATION_DIALOG_TITLE = "Notifications";
    private static final String CLEAR_ALL = "Clear All";

    private MessageBoard messageBoard;
    private PrideInspectorContext context;

    public NotificationDialog(JFrame owner) {
        super(owner, NOTIFICATION_DIALOG_TITLE);
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(570, 400));

        // main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Message board
        messageBoard = new MessageBoard();
        messageBoard.setBackground(Color.white);

        // scroll pane
        JScrollPane scrollPane = new JScrollPane(messageBoard, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // button panel
        JPanel buttonPanel= new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // clear all button
        JButton clearButton = new JButton(CLEAR_ALL);
        clearButton.addActionListener(this);
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.CENTER);

        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - getWidth())/2, (d.height - getHeight())/2);

        // add itself as a listener to task manager
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        ThrowableHandler handler = context.getThrowableHandler();
        handler.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String eventName = evt.getPropertyName();

        // get old and new values
        java.util.List<ThrowableEntry> oldValues = (java.util.List<ThrowableEntry>)evt.getOldValue();
        java.util.List<ThrowableEntry> newValues = (java.util.List<ThrowableEntry>)evt.getNewValue();

        if (ThrowableHandler.ADD_THROWABLE_PROP.equals(eventName)) {
            // get new ones
            newValues.removeAll(oldValues);

            // add new ones
            for (ThrowableEntry newValue : newValues) {
                messageBoard.showMessage(newValue);
            }

        } else if (ThrowableHandler.REMOVE_THROWABLE_PROP.equals(eventName)) {
            // get old ones
            oldValues.removeAll(newValues);

            // remove old ones
            for (ThrowableEntry oldValue : oldValues) {
                messageBoard.removeMessage(oldValue);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(CLEAR_ALL)) {
            context.removeAllThrowableEntries();
        }
    }
}
