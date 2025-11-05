package common.src.dataClasses;
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
    public boolean execute() {
        // Logique pour exécuter la création de message
        // À implémenter selon les règles métier
        return message != null;
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
