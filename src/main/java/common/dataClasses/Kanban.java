package common.dataClasses;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Kanban extends LightKanban {
    private HashMap<Column, List<Task>> taskColumn;

    private List<Message> messages;
    private List<Task> tasks;
    private List<Column> columns;
    private transient User creator;  // transient = not serialized to JSON
    private UUID creatorId;          // Only the ID is saved
    private String visibility; // public / private
    

// Constructeur sans ID et visibility
    public Kanban(String title, List<Access> accessList, String visibility, User creator) {
        super(title, accessList);
        this.taskColumn = new HashMap<>();
        this.visibility = visibility;
        this.creator = creator;
        this.creatorId = creator != null ? creator.getId() : null;
    }
    
    // Constructeur avec ID et visibility
    public Kanban(UUID id, String title, List<Access> accessList, String visibility, User creator) {
        super(id, title, accessList);
        this.visibility = visibility;
        this.taskColumn = new HashMap<>();
        this.creator = creator;
        this.creatorId = creator != null ? creator.getId() : null;
    }

    

    // Constructeur
    public Kanban(String title, List<Access> accessList) {
        super(title, accessList);
        this.taskColumn = new HashMap<>();
    }
    
    // Constructeur avec ID
    public Kanban(UUID id, String title, List<Access> accessList) {
        super(id, title, accessList);
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
    
    public UUID getCreatorId() {
        return creatorId;
    }
    
    public String getVisibility() {
        return visibility;
    }

    public Column getColumnFromID(UUID columnId){
        if (columnId == null || columns == null) return null;
        for (Column col : columns){
            if (col.getId() != null && col.getId().equals(columnId)){
                return col;
            }
        }
        return null;
    }

    public Column getColumnFromTask(UUID taskId){
        for (Map.Entry<Column,List<Task>> entry : taskColumn.entrySet()){
            for (Task task : entry.getValue()){
                if (task.getId().equals(taskId)){
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    
    // Setters
    public void setTaskColumn(HashMap<Column, List<Task>> taskColumn) {
        this.taskColumn = taskColumn;
    }

    public void setCreator(User creator) {
        this.creator = creator;
        this.creatorId = creator != null ? creator.getId() : null;
    }
    
    public void setCreatorId(UUID creatorId) {
        this.creatorId = creatorId;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;

        //Mettre à jour le hashmap des tâches
        // IMPORTANT: Utiliser la recherche par ID pour éviter les problèmes de référence d'objet
        for (Task task : tasks){
            Column col = this.getColumnFromTask(task.getId());
            if (col != null){
                // Chercher la colonne par ID dans taskColumn pour éviter les problèmes de référence
                UUID colId = col.getId();
                Column targetCol = null;
                for (Column c : taskColumn.keySet()) {
                    if (c != null && c.getId() != null && c.getId().equals(colId)) {
                        targetCol = c;
                        break;
                    }
                }
                
                // Si on a trouvé la colonne, utiliser celle-là, sinon utiliser celle retournée par getColumnFromTask
                if (targetCol != null) {
                    List<Task> taskList = taskColumn.getOrDefault(targetCol, new ArrayList<>());
                    if (!taskList.contains(task)){
                        taskList.add(task);
                        taskColumn.put(targetCol, taskList);
                    }
                } else {
                    // Si la colonne n'existe pas dans taskColumn, l'ajouter
                    List<Task> taskList = taskColumn.getOrDefault(col, new ArrayList<>());
                    if (!taskList.contains(task)){
                        taskList.add(task);
                        taskColumn.put(col, taskList);
                    }
                }
            }
        }
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
        // Reconstruire taskColumn en utilisant les objets Column de la liste columns
        // pour garantir la cohérence entre columns et taskColumn
        if (taskColumn == null) {
            taskColumn = new HashMap<>();
        }
        HashMap<Column, List<Task>> newTaskColumn = new HashMap<>();
        // Préserver les tâches existantes en les trouvant par ID
        for (Column col : columns) {
            List<Task> existingTasks = null;
            // Chercher les tâches existantes pour cette colonne par ID
            UUID colId = col.getId();
            if (colId != null) {
                for (Map.Entry<Column, List<Task>> entry : taskColumn.entrySet()) {
                    Column oldCol = entry.getKey();
                    if (oldCol != null && oldCol.getId() != null && oldCol.getId().equals(colId)) {
                        existingTasks = entry.getValue();
                        break;
                    }
                }
            }
            newTaskColumn.put(col, existingTasks != null ? existingTasks : new ArrayList<>());
        }
        this.taskColumn = newTaskColumn;
    }
    
    public void modifyHashmap(List<Task> tasks, Column col){
        taskColumn.put(col, tasks);
        //Mettre à jour la liste des tâches
        this.setTasks(tasks);

        //Mettre à jour la liste des colonnes si nécessaire
        if (!columns.contains(col)){
            columns.add(col);
        }
    }
   
    // Méthode utilitaire pour obtenir toutes les tâches d'une colonne
    public List<Task> getTasksFromColumn(Column column) {
        if (column == null || taskColumn == null) {
            return new ArrayList<>();
        }
        // Chercher par ID au lieu de par référence d'objet
        UUID columnId = column.getId();
        if (columnId == null) {
            return new ArrayList<>();
        }
        for (Map.Entry<Column, List<Task>> entry : taskColumn.entrySet()) {
            Column col = entry.getKey();
            if (col != null && col.getId() != null && col.getId().equals(columnId)) {
                return entry.getValue();
            }
        }
        return new ArrayList<>();
    }
    
    // Méthode utilitaire pour obtenir toutes les colonnes
    public List<Column> getAllColumns() {
        return columns;
    }

    // methode renvoie lightKanban a partir de Kanban
    public LightKanban getLightKanban() { 
        return new LightKanban(this.getId(),this.getTitle(), this.getAccessList());
    }

}