package common.dataClasses;


public class Access {
  private Role role;          
  private LightUser user;

   public Access(LightUser user, Role role) {
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

    // Setters
    public void setRole(Role role) {
        this.role = role;
    }

    public void setUser(LightUser user) {
        this.user = user;
    }
}