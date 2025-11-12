package common.src.dataClasses;
import java.util.UUID;

public class LightKanban {
    private String title;
    private UUID id;
    
    // Constructeur
    public LightKanban(String title) {
        this.id = UUID.randomUUID();
        this.title = title;
    }
    
    // Constructeur avec ID
    public LightKanban(UUID id, String title) {
        this.id = id;
        this.title = title;
    }
    
    // Getters
    public String getTitle() {
        return title;
    }
    
    public UUID getId() {
        return id;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
}
