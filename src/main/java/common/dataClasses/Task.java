package common.dataClasses;
import java.util.UUID;
import java.time.LocalDate;

public class Task {
    private UUID id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LightUser creator;
    private List<LightUser> affectedUsers; 
    
    // Constructeur
    public Task(String title, String description, LocalDate startDate, LocalDate endDate) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }

    public LightUser getCreator() {
        return creator;
    }

    public List<LightUser> getAffectedUsers() {
        return affectedUsers;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setCreator(LightUser creator) {
        this.creator = creator;
    }

    public void setAffectedUsers(List<LightUser> affectedUsers) {
        this.affectedUsers = affectedUsers;
    }
}
