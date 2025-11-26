package common.dataClasses;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Kanban extends LightKanban {
    private HashMap<Column, List<Task>> taskColumn;

    private List<Message> messages;
    private List<Task> tasks;
    private List<Column> columns;
    private User creator; 
    private String visibility; // public / private
    

// Constructeur sans ID et visibility
    public Kanban(String title , String visibility, User creator) {
        super(title);
        this.taskColumn = new HashMap<>();
        this.visibility = visibility;
        this.creator = creator;
    }
    
    // Constructeur avec ID et visibility
    public Kanban(UUID id, String title, String visibility, User creator) {
        super(id, title);
        this.visibility = visibility;
        this.taskColumn = new HashMap<>();
        this.creator = creator;
    }

    

    // Constructeur
    public Kanban(String title) {
        super(title);
        this.taskColumn = new HashMap<>();
    }
    
    // Constructeur avec ID
    public Kanban(UUID id, String title) {
        super(id, title);
        this.taskColumn = new HashMap<>();
    }
    
    // Getters
    public HashMap<Column, List<Task>> getTaskColumn() {
        return taskColumn;
    }

    public List<Message>  getMessages() {
        return messages;
    }
    public List<Task> getTasks() {
        return tasks;
    }
    public List<Column> getColumns() {
        return columns;
    }

    public User getCreator() {
        return creator;
    }
    public String getVisibility() {
        return visibility;
    }
    
    // Setters
    public void setTaskColumn(HashMap<Column, List<Task>> taskColumn) {
        this.taskColumn = taskColumn;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
   
   
    // Méthode utilitaire pour obtenir toutes les tâches d'une colonne
    public List<Task> getTasksFromColumn(Column column) {
        return taskColumn.getOrDefault(column, new ArrayList<>());
    }
    
    // Méthode utilitaire pour obtenir toutes les colonnes
    public List<Column> getAllColumns() {
        return new ArrayList<>(taskColumn.keySet());
    }

    // methode renvoie lightKanban a partir de Kanban
    public LightKanban getLightKanban() { 
        return new LightKanban(this.getId(),this.getTitle());
    }

}