package common.dataClasses;
import java.util.UUID;
import java.util.List;

public class CreateTask extends Modification {
    private Task newTask;
    private UUID targetColumn;
    
    // Constructeur
    public CreateTask(Task newTask, UUID targetColumn) {
        super();
        this.newTask = newTask;
        this.targetColumn = targetColumn;
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
    
    // Setters
    public void setNewTask(Task newTask) {
        this.newTask = newTask;
    }
    
    public void setTargetColumn(UUID targetColumn) {
        this.targetColumn = targetColumn;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        Column col = targetKanban.getColumnFromID(targetColumn);
        List<Task> listTasks = targetKanban.getTasks();
        listTasks.add(newTask);
        targetKanban.modifyHashmap(listTasks, col);
        return targetKanban;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la création de tâche
        // À implémenter selon les règles métier
        return newTask != null && targetColumn != null;
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
