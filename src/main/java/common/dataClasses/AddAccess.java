package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class AddAccess extends Modification {
    private Access acces;
    private UUID previousAccessId = null;
    
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
    public UUID getPreviousAccessId() {
        return previousAccessId;
    }
    
    // Setters
    public void setAcces(Access acces) {
        this.acces = acces;
    }
    public void setPreviousAccessId(UUID previousAccessId) {
        this.previousAccessId = previousAccessId;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        LightKanban lightKanban = targetKanban.getLightKanban();
        this.previousAccessId = acces.getId();
        if(!lightKanban.getAccessList().contains(acces)){
            List<Access> accesLightKanban = lightKanban.getAccessList();
            accesLightKanban.add(acces);
            lightKanban.setAccessList(accesLightKanban);
        }
        
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        DeleteAccess undoModification = new DeleteAccess(previousAccessId);
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "AddAccess{" +
                "id=" + getId() +
                ", acces =" + (acces != null ? acces.getRole() : "null") +
                '}';
    }
}
