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
        //supprimer la tache avec le meme id
        taskList.removeIf(t -> t.getId().equals(task.getId()));
        //ajouter la tache modifiée
        taskList.add(task);
        targetKanban.setTasks(taskList);
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
