package common.src.dataClasses;
import java.util.UUID;

public class RemoveAccess extends Modification {
    private LightKanban lightKanban;
    
    // Constructeur
    public RemoveAccess(LightKanban lightKanban) {
        super();
        this.lightKanban = lightKanban;
    }
    
    // Constructeur avec ID
    public RemoveAccess(UUID id, LightKanban lightKanban) {
        super(id);
        this.lightKanban = lightKanban;
    }
    
    // Getters
    public LightKanban getLightKanban() {
        return lightKanban;
    }
    
    // Setters
    public void setLightKanban(LightKanban lightKanban) {
        this.lightKanban = lightKanban;
    }
    
    @Override
    public boolean execute() {
        // Logique pour exécuter la suppression d'accès
        // À implémenter selon les règles métier
        return lightKanban != null;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la suppression d'accès
        // À implémenter selon les règles métier
        return lightKanban != null;
    }
    
    @Override
    public String toString() {
        return "RemoveAccess{" +
                "id=" + getId() +
                ", lightKanban=" + (lightKanban != null ? lightKanban.getTitle() : "null") +
                '}';
    }
}
