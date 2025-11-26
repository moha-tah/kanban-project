package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class CreateMessage extends Modification {
    private Message message;
    
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
    
    // Setters
    public void setMessage(Message message) {
        this.message = message;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        List<Message> messageList = targetKanban.getMessages();
        messageList.add(message);
        targetKanban.setMessages(messageList);
        return targetKanban;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la création de message
        // À implémenter selon les règles métier
        return message != null;
    }
    
    @Override
    public String toString() {
        return "CreateMessage{" +
                "id=" + getId() +
                ", message=" + (message != null ? message.getContent() : "null") +
                '}';
    }
}
