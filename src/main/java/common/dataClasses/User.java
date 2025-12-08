package common.dataClasses;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class User extends LightUser {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private List<Kanban> myKanban;

    

    public User(String username, String firstName, String lastName, LocalDate birthDate) {
        super(username);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.myKanban = new ArrayList<>();
    }

    public User(UUID id, String username, String firstName, String lastName, LocalDate birthDate) {
        super(id, username);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.myKanban = new ArrayList<>();
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public List<Kanban> getMyKanban() { return myKanban; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setMyKanban(List<Kanban> myKanban) { this.myKanban = myKanban; }

    public boolean canModifyProfile(LightUser currentUser) {
        return this.getId().equals(currentUser.getId());
    }

    public boolean modifyProfile(LightUser currentUser, String newFirstName, String newLastName, LocalDate newBirthDate) {
        if (canModifyProfile(currentUser)) {
            this.firstName = newFirstName;
            this.lastName = newLastName;
            this.birthDate = newBirthDate;
            return true;
        }
        return false;
    }

    public void addKanban(Kanban kanban) {
        if (this.myKanban == null) {
            this.myKanban = new ArrayList<>();
        }
        // Éviter les doublons
        boolean exists = this.myKanban.stream().anyMatch(k -> k.getId().equals(kanban.getId()));
        if (!exists) {
            this.myKanban.add(kanban);
        }
    }

    public boolean removeKanban(Kanban kanban) {
        return myKanban.remove(kanban);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}