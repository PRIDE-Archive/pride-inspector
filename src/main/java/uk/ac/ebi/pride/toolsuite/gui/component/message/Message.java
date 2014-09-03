package uk.ac.ebi.pride.toolsuite.gui.component.message;

/**
 * A message
 *
 * User: rwang
 * Date: 16-Nov-2010
 * Time: 11:36:37
 */
public class Message {
    /**
     * Message type
     */
    private MessageType type;
    /**
     * Message body
     */
    private String message;

    /**
     * Default message type id INFO
     *
     * @param message   message
     */
    public Message(String message) {
        this.type = MessageType.INFO;
        this.message = message;
    }

    /**
     * Create a type with a null message.
     *
     * @param type  message type
     */
    public Message(MessageType type) {
        this.type = type;
        this.message = null;
    }

    /**
     * Constructor
     * @param type  type fo the message
     * @param message   message
     */
    public Message(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
