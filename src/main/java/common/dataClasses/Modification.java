package common.dataClasses;
import java.io.Serializable;
import java.util.UUID;


public abstract class Modification implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private LightKanban lightTargetKanban;
    
    // ConstructeurL
    public Modification() {
        this.id = UUID.randomUUID();
    }
    
    // Constructeur avec ID
    public Modification(UUID id) {
        this.id = id;
    }
    
    // Getters
    public UUID getId() {
        return id;
    }

    public LightKanban getLightTargetKanban() {
        return lightTargetKanban;
    }


    
    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setLightTargetKanban(LightKanban myKanban) {
        this.lightTargetKanban = myKanban;
    }

    
    // Méthode abstraite pour exécuter la modification
    public abstract Kanban execute(Kanban targetKanban);
    
    // Méthode abstraite pour annuler la modification
    public abstract Kanban undo(Kanban targetKanban);
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Modification that = (Modification) obj;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }
}
