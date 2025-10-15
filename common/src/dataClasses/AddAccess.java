package common.src.dataClasses;
import java.util.UUID;

public class AddAccess extends Modification {
    private LightKanban lightKanban;
    
    // Constructeur
    public AddAccess(LightKanban lightKanban) {
        super();
        this.lightKanban = lightKanban;
    }
    
    // Constructeur avec ID
    public AddAccess(UUID id, LightKanban lightKanban) {
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
        // Logique pour exécuter l'ajout d'accès
        // À implémenter selon les règles métier
        return lightKanban != null;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler l'ajout d'accès
        // À implémenter selon les règles métier
        return lightKanban != null;
    }
    
    @Override
    public String toString() {
        return "AddAccess{" +
                "id=" + getId() +
                ", lightKanban=" + (lightKanban != null ? lightKanban.getTitle() : "null") +
                '}';
    }
}
