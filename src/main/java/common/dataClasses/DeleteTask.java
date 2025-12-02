package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class DeleteTask extends Modification {
    private UUID taskId;
    private Task previousTask = null;
    
    // Constructeur
    public DeleteTask(UUID taskId) {
        super();
        this.taskId = taskId;
    }
    
    // Constructeur avec ID
    public DeleteTask(UUID id, UUID taskId) {
        super(id);
        this.taskId = taskId;
    }
    
    // Getters
    public UUID getTaskId() {
        return taskId;
    }
    public Task getPreviousTask() {
        return previousTask;
    }
    
    // Setters
    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
    public void setPreviousTask(Task previousTask) {
        this.previousTask = previousTask;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        //enlever la tâche de la liste de tâches 
        List<Task> tasks = targetKanban.getTasks();
        this.previousTask = tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);
        tasks.removeIf(task -> task.getId().equals(taskId));
        targetKanban.setTasks(tasks);

        //récupérer la colonne de la tâche
        Column col = targetKanban.getColumnFromTask(taskId);

        //mettre à jour le hashmap en enlevant la tâche pour sa colonne
        targetKanban.modifyHashmap(tasks, col);

        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        CreateTask undoModification = new CreateTask(previousTask, targetKanban.getColumnFromTask(previousTask.getId()).getId());
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "DeleteTask{" +
                "id=" + getId() +
                ", taskId=" + taskId +
                '}';
    }
}
