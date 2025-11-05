package common.src.dataClasses;
import java.util.UUID;
import java.time.LocalDateTime;

public class Message {
    private UUID id;
    private String content;
    private LocalDateTime date;
    
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
    
    // Setters
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
