package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class AddAccess extends Modification {
    private Access access;
    private UUID previousAccessId = null;
    
    // Constructeur
    public AddAccess(Access access) {
        super();
        this.access = access;
    }
    
    // Constructeur avec kanban cible
    public AddAccess(Access access, LightKanban targetKanban) {
        super();
        this.access = access;
        this.setLightTargetKanban(targetKanban);
    }
    
    // Constructeur avec ID
    public AddAccess(UUID id, Access access) {
        super(id);
        this.access = access;
    }
    
    // Getters
    public Access getAccess() {
        return access;
    }
    public UUID getPreviousAccessId() {
        return previousAccessId;
    }
    
    // Setters
    public void setAccess(Access access) {
        this.access = access;
    }
    public void setPreviousAccessId(UUID previousAccessId) {
        this.previousAccessId = previousAccessId;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        LightKanban lightKanban = targetKanban.getLightKanban();
        this.previousAccessId = access.getId();
        if(lightKanban.getAccessList().stream().noneMatch(a -> a.getId().equals(access.getId()))){
            List<Access> accessLightKanban = lightKanban.getAccessList();
            accessLightKanban.add(access);
            lightKanban.setAccessList(accessLightKanban);
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
                ", access =" + (access != null ? access.getRole() : "null") +
                '}';
    }
}
