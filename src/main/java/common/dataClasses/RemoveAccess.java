package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class RemoveAccess extends Modification {
    private Access acces;
    
    // Constructeur
    public RemoveAccess(Access acces) {
        super();
        this.acces = acces;
    }
    
    // Constructeur avec ID
    public RemoveAccess(UUID id, Access acces) {
        super(id);
        this.acces = acces;
    }
    
    // Getters
    public Access getAccess() {
        return acces;
    }
    
    // Setters
    public void setLightKanban(Access acces) {
        this.acces = acces;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        LightKanban lightKanban = targetKanban.getLightKanban();

        if(lightKanban.getAccessList().contains(acces)){
            List<Access> accesLightKanban = lightKanban.getAccessList();
            accesLightKanban.remove(acces);
            lightKanban.setAccessList(accesLightKanban);
        }

        return targetKanban;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la suppression d'accès
        // À implémenter selon les règles métier
        return acces != null;
    }
    
    @Override
    public String toString() {
        return "RemoveAccess{" +
                "id=" + getId() +
                ", acces=" + (acces != null ? acces.getRole() : "null") +
                '}';
    }
}
