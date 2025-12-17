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
        List<Task> listTasks = targetKanban.getTasks();
        listTasks.add(newTask);
        targetKanban.modifyHashmap(listTasks, col);
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
