package common.src.dataClasses;
import java.util.UUID;

public abstract class Modification {
    private UUID id;
    
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
    
    // Setters
    public void setId(UUID id) {
        this.id = id;
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
