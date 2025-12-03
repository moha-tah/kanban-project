package common.dataClasses;
import java.util.UUID;


public abstract class Modification {
    private UUID id;
    private LightKanban myKanban;
    
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

    public LightKanban getMyKanban() {
        return myKanban;
    }


    
    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setMyKanban(LightKanban myKanban) {
        this.myKanban = myKanban;
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
