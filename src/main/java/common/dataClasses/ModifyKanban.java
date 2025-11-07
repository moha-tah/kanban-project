package common.src.dataClasses;
import java.util.UUID;

public class ModifyKanban extends Modification {
    private LightKanban kanban;
    
    // Constructeur
    public ModifyKanban(LightKanban kanban) {
        super();
        this.kanban = kanban;
    }
    
    // Constructeur avec ID
    public ModifyKanban(UUID id, LightKanban kanban) {
        super(id);
        this.kanban = kanban;
    }
    
    // Getters
    public LightKanban getKanban() {
        return kanban;
    }
    
    // Setters
    public void setKanban(LightKanban kanban) {
        this.kanban = kanban;
    }
    
    @Override
    public boolean execute() {
        // Logique pour exécuter la modification de kanban
        // À implémenter selon les règles métier
        return kanban != null;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la modification de kanban
        // À implémenter selon les règles métier
        return kanban != null;
    }
    
    @Override
    public String toString() {
        return "ModifyKanban{" +
                "id=" + getId() +
                ", kanban=" + (kanban != null ? kanban.getTitle() : "null") +
                '}';
    }
}
