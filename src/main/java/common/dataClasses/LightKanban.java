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
    public LightKanban(String title, List<Access> accessList) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.accessList = accessList;
    }
    
    // Constructeur avec ID
    public LightKanban(UUID id, String title, List<Access> accessList) {
        this.id = id;
        this.title = title;
        this.accessList = accessList;
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
        // Check if the user has a role that allows modification
        if (accessList == null || user == null) {
            return false;
        }
        for (Access access : accessList) {
            if (access.getUser() != null && access.getUser().getId() != null && 
                user.getId() != null && access.getUser().getId().equals(user.getId())) {
                Role role = access.getRole();
                if (role == Role.MODIFIER) {
                    return true;
                }
            }
        }
        return false;
    }


}
