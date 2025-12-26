package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class ModifyTask extends Modification {
    private Task task;
    private Task previousTask = null;
    
    // Constructeur
    public ModifyTask(Task task) {
        super();
        this.task = task;
    }
    
    // Constructeur avec kanban cible
    public ModifyTask(Task task, LightKanban targetKanban) {
        super();
        this.task = task;
        this.setLightTargetKanban(targetKanban);
    }
    
    // Constructeur avec ID
    public ModifyTask(UUID id, Task task) {
        super(id);
        this.task = task;
    }
    
    // Getters
    public Task getTask() {
        return task;
    }
    public Task getPreviousTask() {
        return previousTask;
    }
    // Setters
    public void setTask(Task task) {
        this.task = task;
    }
    public void setPreviousTask(Task previousTask) {
        this.previousTask = previousTask;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        List<Task> taskList = targetKanban.getTasks();
        this.previousTask = taskList.stream()
                .filter(t -> t.getId().equals(task.getId()))
                .findFirst()
                .orElse(null);
        
        // Trouver la colonne actuelle de la tâche AVANT de la retirer
        Column currentColumn = targetKanban.getColumnFromTask(task.getId());
        
        // Supprimer la tâche de la liste
        taskList.removeIf(t -> t.getId().equals(task.getId()));
        
        // Retirer la tâche de l'ancienne colonne dans taskColumn
        if (currentColumn != null) {
            java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
            List<Task> columnTasks = taskColumn.get(currentColumn);
            if (columnTasks != null) {
                columnTasks.removeIf(t -> t.getId().equals(task.getId()));
                taskColumn.put(currentColumn, columnTasks);
            }
        }
        
        // Ajouter la tâche modifiée à la liste
        taskList.add(task);
        
        // Ajouter la tâche modifiée à la colonne actuelle dans taskColumn
        if (currentColumn != null) {
            java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
            List<Task> columnTasks = taskColumn.getOrDefault(currentColumn, new java.util.ArrayList<>());
            // Vérifier que la tâche n'est pas déjà dans la liste (au cas où)
            columnTasks.removeIf(t -> t.getId().equals(task.getId()));
            columnTasks.add(task);
            taskColumn.put(currentColumn, columnTasks);
            targetKanban.setTaskColumn(taskColumn);
        }
        
        // Mettre à jour la liste des tâches
        // IMPORTANT: Ne pas utiliser setTasks() car elle utilise getColumnFromTask() qui peut
        // trouver l'ancienne colonne si la HashMap n'est pas synchronisée
        // On met à jour directement la liste et on s'assure que taskColumn est déjà à jour
        targetKanban.setTasks(taskList);
        
        // S'assurer que la tâche n'est que dans la colonne actuelle
        // Retirer la tâche de toutes les autres colonnes
        if (currentColumn != null) {
            java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
            for (java.util.Map.Entry<Column, List<Task>> entry : taskColumn.entrySet()) {
                Column col = entry.getKey();
                if (!col.getId().equals(currentColumn.getId())) {
                    List<Task> columnTasks = entry.getValue();
                    if (columnTasks != null) {
                        boolean removed = columnTasks.removeIf(t -> t.getId().equals(task.getId()));
                        if (removed) {
                            taskColumn.put(col, columnTasks);
                        }
                    }
                }
            }
            targetKanban.setTaskColumn(taskColumn);
        }
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        ModifyTask undoModification = new ModifyTask(previousTask);
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "ModifyTask{" +
                "id=" + getId() +
                ", task=" + (task != null ? task.getTitle() : "null") +
                '}';
    }
}
