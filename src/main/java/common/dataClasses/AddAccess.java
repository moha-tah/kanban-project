package common.dataClasses;
import java.util.UUID;
import java.util.List;

public class AddAccess extends Modification {
    private Access acces;
    
    // Constructeur
    public AddAccess(Access acces) {
        super();
        this.acces = acces;
    }
    
    // Constructeur avec ID
    public AddAccess(UUID id, Access acces) {
        super(id);
        this.acces = acces;
    }
    
    // Getters
    public Access getAcces() {
        return acces;
    }
    
    // Setters
    public void setAcces(Access acces) {
        this.acces = acces;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        LightKanban lightKanban = targetKanban.getLightKanban();

        if(!lightKanban.getAccessList().contains(acces)){
            List<Access> accesLightKanban = lightKanban.getAccessList();
            accesLightKanban.add(acces);
            lightKanban.setAccessList(accesLightKanban);
        }
        
        return targetKanban;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler l'ajout d'accès
        // À implémenter selon les règles métier
        return acces != null;
    }
    
    @Override
    public String toString() {
        return "AddAccess{" +
                "id=" + getId() +
                ", acces =" + (acces != null ? acces.getRole() : "null") +
                '}';
    }
}
