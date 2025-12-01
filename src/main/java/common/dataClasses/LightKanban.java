package common.dataClasses;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class LightKanban implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private UUID id;
    private List<Access> accessList;
    
    // Constructeur
    public LightKanban(String title, List<Access> accesList) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.accessList = accesList;
    }
    
    // Constructeur avec ID
    public LightKanban(UUID id, String title, List<Access> acces) {
        this.id = id;
        this.title = title;
        this.accessList = acces;
    }
    
    // Getters
    public String getTitle() {
        return title;
    }
    
    public UUID getId() {
        return id;
    }
    public List<Access> getAccessList() {
        return accessList;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    public void setAccessList(List<Access> accessList) {
        this.accessList = accessList;
    }

     public boolean canBeModifiedBy(LightUser user) {
        // Logique pour vérifier si l'utilisateur peut modifier le kanban
        // À implémenter selon les règles métier
        return true; // Placeholder
    }


}
