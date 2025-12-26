package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class CreateTask extends Modification {
    private Task newTask;
    private UUID targetColumn;
    private UUID previousTaskId = null;
    
    // Constructeur
    public CreateTask(Task newTask, UUID targetColumn) {
        super();
        this.newTask = newTask;
        this.targetColumn = targetColumn;
    }
    
    // Constructeur avec kanban cible
    public CreateTask(Task newTask, UUID targetColumn, LightKanban targetKanban) {
        super();
        this.newTask = newTask;
        this.targetColumn = targetColumn;
        this.setLightTargetKanban(targetKanban);
    }
    
    // Constructeur avec ID
    public CreateTask(UUID id, Task newTask, UUID targetColumn) {
        super(id);
        this.newTask = newTask;
        this.targetColumn = targetColumn;
    }
    
    // Getters
    public Task getNewTask() {
        return newTask;
    }
    
    public UUID getTargetColumn() {
        return targetColumn;
    }

    public UUID getPreviousTaskId() {
        return previousTaskId;
    }
    
    // Setters
    public void setNewTask(Task newTask) {
        this.newTask = newTask;
    }
    
    public void setTargetColumn(UUID targetColumn) {
        this.targetColumn = targetColumn;
    }

    public void setPreviousTaskId(UUID previousTaskId) {
        this.previousTaskId = previousTaskId;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        Column col = targetKanban.getColumnFromID(targetColumn);
        this.previousTaskId = newTask.getId();
        
        // Ajouter la tâche à la liste des tâches
        List<Task> listTasks = targetKanban.getTasks();
        if (listTasks == null) {
            listTasks = new java.util.ArrayList<>();
        }
        listTasks.add(newTask);
        
        // Ajouter la tâche à la colonne dans taskColumn
        if (col != null) {
            java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
            if (taskColumn == null) {
                taskColumn = new java.util.HashMap<>();
            }
            
            // Trouver la colonne par ID dans taskColumn (au cas où l'objet Column serait différent)
            Column targetCol = null;
            UUID colId = col.getId();
            for (Column c : taskColumn.keySet()) {
                if (c != null && c.getId() != null && c.getId().equals(colId)) {
                    targetCol = c;
                    break;
                }
            }
            
            // Si la colonne n'existe pas dans taskColumn, utiliser celle passée en paramètre
            if (targetCol == null) {
                targetCol = col;
            }
            
            // Ajouter la tâche à la colonne
            List<Task> columnTasks = taskColumn.getOrDefault(targetCol, new java.util.ArrayList<>());
            // S'assurer que la tâche n'est pas déjà dans la colonne (au cas où)
            columnTasks.removeIf(t -> t.getId().equals(newTask.getId()));
            columnTasks.add(newTask);
            taskColumn.put(targetCol, columnTasks);
            targetKanban.setTaskColumn(taskColumn);
        }
        
        // Mettre à jour directement la liste des tâches
        // IMPORTANT: Ne pas utiliser setTasks() car elle utilise getColumnFromTask() qui peut
        // réajouter la tâche dans une mauvaise colonne si taskColumn n'est pas synchronisé
        // On met à jour directement le champ tasks
        targetKanban.setTasks(listTasks);
        
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        DeleteTask undoModification = new DeleteTask(previousTaskId, targetColumn);
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "CreateTask{" +
                "id=" + getId() +
                ", newTask=" + (newTask != null ? newTask.getTitle() : "null") +
                ", targetColumn=" + targetColumn +
                '}';
    }
}
