package common.dataClasses;
import java.io.Serializable;
import java.util.UUID;


public abstract class Modification implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private LightKanban targetKanban;
    
    // Constructeur
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

    public LightKanban getTargetKanban() {
        return targetKanban;
    }
    
    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setTargetKanban(LightKanban targetKanban) {
        this.targetKanban = targetKanban;
    }
    
    // Méthode abstraite pour exécuter la modification
    public abstract boolean execute();
    
    // Méthode abstraite pour annuler la modification
    public abstract boolean undo();
    
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
