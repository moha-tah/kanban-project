package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class AddUserToTask extends Modification {
    private UUID taskId;
    private UUID userId;
    private LightUser previousUser = null;
    
    // Constructeur
    public AddUserToTask(UUID taskId, UUID userId) {
        super();
        this.taskId = taskId;
        this.userId = userId;
    }
    
    // Constructeur avec kanban cible
    public AddUserToTask(UUID taskId, UUID userId, LightKanban targetKanban) {
        super();
        this.taskId = taskId;
        this.userId = userId;
        this.setLightTargetKanban(targetKanban);
    }
    
    // Constructeur avec ID
    public AddUserToTask(UUID id, UUID taskId, UUID userId) {
        super(id);
        this.taskId = taskId;
        this.userId = userId;
    }
    
    // Getters
    public UUID getTaskId() {
        return taskId;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public LightUser getPreviousUser() {
        return previousUser;
    }
    
    // Setters
    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public void setPreviousUser(LightUser previousUser) {
        this.previousUser = previousUser;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        // Trouver la tâche
        Task task = targetKanban.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);
        
        if (task == null) {
            return targetKanban;
        }
        
        // Trouver l'utilisateur dans la liste des utilisateurs connectés
        // On doit chercher dans les kanbans ou utiliser une autre méthode
        // Pour l'instant, on va créer un LightUser avec juste l'ID
        // Le serveur devra compléter les informations
        
        // Initialiser la liste si elle est null
        List<LightUser> affectedUsers = task.getAffectedUsers();
        if (affectedUsers == null) {
            affectedUsers = new java.util.ArrayList<>();
        }
        
        // Vérifier si l'utilisateur n'est pas déjà dans la liste
        boolean userExists = affectedUsers.stream()
                .anyMatch(u -> u.getId().equals(userId));
        
        if (!userExists) {
            // Chercher l'utilisateur dans les accessList des kanbans pour obtenir ses informations complètes
            // Si on ne le trouve pas, créer un LightUser temporaire avec juste l'ID
            LightUser userToAdd = null;
            
            // Chercher dans l'accessList du kanban
            if (targetKanban.getAccessList() != null) {
                for (Access access : targetKanban.getAccessList()) {
                    if (access.getUser() != null && access.getUser().getId().equals(userId)) {
                        userToAdd = access.getUser();
                        break;
                    }
                }
            }
            
            // Si on ne l'a pas trouvé, créer un LightUser temporaire
            // Le serveur devra compléter les informations (username, avatar, etc.)
            if (userToAdd == null) {
                userToAdd = new LightUser(userId, "User " + userId.toString().substring(0, 8));
            }
            
            affectedUsers.add(userToAdd);
            task.setAffectedUsers(affectedUsers);
            
            // Mettre à jour la tâche dans le kanban en utilisant la même logique que ModifyTask
            // pour éviter les problèmes de duplication
            List<Task> taskList = targetKanban.getTasks();
            Column currentColumn = targetKanban.getColumnFromTask(taskId);
            
            // Retirer la tâche de l'ancienne colonne dans taskColumn
            if (currentColumn != null) {
                java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
                List<Task> columnTasks = taskColumn.get(currentColumn);
                if (columnTasks != null) {
                    columnTasks.removeIf(t -> t.getId().equals(taskId));
                    taskColumn.put(currentColumn, columnTasks);
                }
            }
            
            // Supprimer la tâche de la liste
            taskList.removeIf(t -> t.getId().equals(taskId));
            
            // Ajouter la tâche modifiée à la liste
            taskList.add(task);
            
            // Ajouter la tâche modifiée à la colonne actuelle dans taskColumn
            if (currentColumn != null) {
                java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
                List<Task> columnTasks = taskColumn.getOrDefault(currentColumn, new java.util.ArrayList<>());
                columnTasks.removeIf(t -> t.getId().equals(taskId));
                columnTasks.add(task);
                taskColumn.put(currentColumn, columnTasks);
                targetKanban.setTaskColumn(taskColumn);
            }
            
            // Mettre à jour directement le champ tasks
            // IMPORTANT: Ne pas utiliser setTasks() car elle peut créer des doublons
            // en réajoutant toutes les tâches à leur colonne respective
            // On met à jour directement le champ via setTasks() mais on s'assure
            // que taskColumn est déjà à jour et synchronisé
            // Pour éviter les doublons, on met à jour taskColumn AVANT setTasks()
            targetKanban.setTasks(taskList);
            
            // S'assurer que la tâche n'est que dans la colonne actuelle
            if (currentColumn != null) {
                java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
                for (java.util.Map.Entry<Column, List<Task>> entry : taskColumn.entrySet()) {
                    Column col = entry.getKey();
                    if (!col.getId().equals(currentColumn.getId())) {
                        List<Task> columnTasks = entry.getValue();
                        if (columnTasks != null) {
                            boolean removed = columnTasks.removeIf(t -> t.getId().equals(taskId));
                            if (removed) {
                                taskColumn.put(col, columnTasks);
                            }
                        }
                    }
                }
                targetKanban.setTaskColumn(taskColumn);
            }
        }
        
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        // Pour undo, on devrait retirer l'utilisateur
        // Mais on n'a pas sauvegardé l'état précédent de la liste
        // On va simplement retirer l'utilisateur
        Task task = targetKanban.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElse(null);
        
        if (task != null) {
            List<LightUser> affectedUsers = task.getAffectedUsers();
            if (affectedUsers != null) {
                affectedUsers.removeIf(u -> u.getId().equals(userId));
                task.setAffectedUsers(affectedUsers);
                
                // Mettre à jour la tâche dans le kanban
                List<Task> tasks = targetKanban.getTasks();
                tasks.removeIf(t -> t.getId().equals(taskId));
                tasks.add(task);
                
                // Mettre à jour taskColumn
                Column currentColumn = targetKanban.getColumnFromTask(taskId);
                if (currentColumn != null) {
                    java.util.HashMap<Column, List<Task>> taskColumn = targetKanban.getTaskColumn();
                    List<Task> columnTasks = taskColumn.get(currentColumn);
                    if (columnTasks != null) {
                        columnTasks.removeIf(t -> t.getId().equals(taskId));
                        columnTasks.add(task);
                        taskColumn.put(currentColumn, columnTasks);
                        targetKanban.setTaskColumn(taskColumn);
                    }
                }
                
                targetKanban.setTasks(tasks);
            }
        }
        
        return targetKanban;
    }
    
    @Override
    public String toString() {
        return "AddUserToTask{" +
                "id=" + getId() +
                ", taskId=" + taskId +
                ", userId=" + userId +
                '}';
    }
}

