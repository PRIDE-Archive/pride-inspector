package uk.ac.ebi.pride.toolsuite.gui.component.message;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * MessageBoard to display a list of messages.
 * <p/>
 * User: rwang
 * Date: 12-Nov-2010
 * Time: 10:58:37
 */
public class MessageBoard extends JPanel {

    private final Set<MessagePanel> messagePanels;

    public MessageBoard() {
        this.messagePanels = Collections.synchronizedSet(new HashSet<MessagePanel>());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    /**
     * Show a particular message.
     *
     * @param message message
     */
    public void showMessage(Message message) {
        MessagePanel panel = new MessagePanel(message);
        this.add(panel);
        messagePanels.add(panel);
        validateAndPaint();
    }

    /**
     * Remove a message from the message board.
     *
     * @param message message to remove
     */
    public void removeMessage(Message message) {
        Iterator<MessagePanel> iter = messagePanels.iterator();
        while (iter.hasNext()) {
            MessagePanel panel = iter.next();
            if (panel.getMessage().equals(message)) {
                this.remove(panel);
                iter.remove();
            }
        }
        validateAndPaint();
    }

    /**
     * Remove all messages in the message board
     */
    public void removeAllMessages() {
        this.removeAll();
        messagePanels.clear();
        validateAndPaint();
    }

    public void validateAndPaint() {
        this.revalidate();
        this.repaint();
    }

    /**
     * MessagePanel to display message in different color
     */
    private static class MessagePanel extends JPanel {
        /**
         * Message to display
         */
        private Message message;

        /**
         * Constructor for MessagePanel
         *
         * @param message message
         */
        public MessagePanel(Message message) {
            this.message = message;
            this.setMaximumSize(new Dimension(600, 100));
            this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));
            this.setLayout(new BorderLayout());
            this.setOpaque(false);

            // container contains all the components
            JPanel container = new JPanel(new BorderLayout());
            container.setOpaque(false);
            container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // icon label
            JPanel iconPanel = new JPanel(new GridBagLayout());
            iconPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
            iconPanel.setOpaque(false);
            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(message.getType().getIcon());
            iconPanel.add(iconLabel);
            container.add(iconPanel, BorderLayout.WEST);

            // textPane to display message
            JTextPane textPane = new JTextPane();
            textPane.setContentType("text/html");
            textPane.setText(message.getMessage());
            textPane.setEditable(false);
            textPane.setOpaque(false);
            container.add(textPane, BorderLayout.CENTER);

            this.add(container, BorderLayout.CENTER);
        }

        public Message getMessage() {
            return message;
        }
    }
}
