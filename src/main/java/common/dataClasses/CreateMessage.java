package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class CreateMessage extends Modification {
    private Message message;
    private UUID previousMessageId = null;
    
    // Constructeur
    public CreateMessage(Message message) {
        super();
        this.message = message;
    }
    
    // Constructeur avec ID
    public CreateMessage(UUID id, Message message) {
        super(id);
        this.message = message;
    }
    
    // Getters
    public Message getMessage() {
        return message;
    }
    public UUID getPreviousMessageId() {
        return previousMessageId;
    }
    
    // Setters
    public void setMessage(Message message) {
        this.message = message;
    }
    public void setPreviousMessageId(UUID previousMessageId) {
        this.previousMessageId = previousMessageId;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        List<Message> messageList = targetKanban.getMessages();
        this.previousMessageId = message.getId();
        messageList.add(message);
        targetKanban.setMessages(messageList);
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        DeleteMessage undoModification = new DeleteMessage(previousMessageId);
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "CreateMessage{" +
                "id=" + getId() +
                ", message=" + (message != null ? message.getContent() : "null") +
                '}';
    }
}
