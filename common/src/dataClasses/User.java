package common.src.dataClasses;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class User extends LightUser {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String avatar;
    private List<Kanban> myKanban;
    
    // Constructeur
    public User(String username, String firstName, String lastName, LocalDate birthDate) {
        super(username);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.avatar = "";
        this.myKanban = new ArrayList<>();
    }
    
    // Constructeur avec ID
    public User(UUID id, String username, String firstName, String lastName, LocalDate birthDate) {
        super(id, username);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.avatar = "";
        this.myKanban = new ArrayList<>();
    }
    
    // Getters
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public List<Kanban> getMyKanban() {
        return myKanban;
    }
    
    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public void setMyKanban(List<Kanban> myKanban) {
        this.myKanban = myKanban;
    }
    
    // Méthodes métier
    public boolean canModifyProfile(LightUser currentUser) {
        // Un utilisateur ne peut modifier que son propre profil
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
    
    // Méthode utilitaire pour ajouter un kanban
    public void addKanban(Kanban kanban) {
        if (!myKanban.contains(kanban)) {
            myKanban.add(kanban);
        }
    }
    
    // Méthode utilitaire pour supprimer un kanban
    public boolean removeKanban(Kanban kanban) {
        return myKanban.remove(kanban);
    }
    
    // Méthode utilitaire pour obtenir le nom complet
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
