package common.dataClasses;
import java.util.UUID;
import java.time.LocalDateTime;

public class Message {
    private UUID id;
    private String content;
    private LocalDateTime date;
    private LightUser receiver;
    private LightUser sender;
    
    // Constructeur
    public Message(String content) {
        this.id = UUID.randomUUID();
        this.content = content;
        this.date = LocalDateTime.now();
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDateTime getDate() {
        return date;
    }

    public LightUser getReceiver(){
        return receiver;
    }

    public LightUser getSender(){
        return sender;
    }
    
    // Setters
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setReceiver(LightUser receiver){
        this.receiver = receiver;
    }

    public void setSender(LightUser sender){
        this.sender = sender;
    }
}
