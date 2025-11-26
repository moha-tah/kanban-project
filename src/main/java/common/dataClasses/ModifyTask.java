package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class ModifyTask extends Modification {
    private Task task;
    
    // Constructeur
    public ModifyTask(Task task) {
        super();
        this.task = task;
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
    
    // Setters
    public void setTask(Task task) {
        this.task = task;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        List<Task> taskList = targetKanban.getTasks();
        //supprimer la tache avec le meme id
        taskList.removeIf(t -> t.getId().equals(task.getId()));
        //ajouter la tache modifiée
        taskList.add(task);
        targetKanban.setTasks(taskList);
        return targetKanban;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la modification de tâche
        // À implémenter selon les règles métier
        return task != null;
    }
    
    @Override
    public String toString() {
        return "ModifyTask{" +
                "id=" + getId() +
                ", task=" + (task != null ? task.getTitle() : "null") +
                '}';
    }
}
