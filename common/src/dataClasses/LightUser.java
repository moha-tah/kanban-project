package common.src.dataClasses;
import java.util.UUID;

public class LightUser {
    private UUID id;
    private String username;
    
    // Constructeur
    public LightUser(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
    }
    
    // Constructeur avec ID
    public LightUser(UUID id, String username) {
        this.id = id;
        this.username = username;
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    // Setters
    public void setId(UUID id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LightUser lightUser = (LightUser) obj;
        return id.equals(lightUser.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
