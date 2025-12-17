package common.dataClasses;
import java.io.Serializable;
import java.util.UUID;

public class Access implements Serializable  {
    private UUID id;
  private static final long serialVersionUID = 1L;
  private Role role;          
  private LightUser user;

   public Access(LightUser user, Role role)  {
        this.user = user;
        this.role = role;
    }

    // Getters
    public Role getRole() {
        return role;
    }

    public LightUser getUser() {
        return user;
    }

    public UUID getId() {
        return id;
    }
    // Setters
    public void setRole(Role role) {
        this.role = role;
    }

    public void setUser(LightUser user) {
        this.user = user;
    }
    public void setId(UUID id) {
        this.id = id;
    }
}