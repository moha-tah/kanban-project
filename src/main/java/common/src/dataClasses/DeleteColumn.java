package common.src.dataClasses;
import java.util.UUID;

public class DeleteColumn extends Modification {
    private UUID columnId;
    
    // Constructeur
    public DeleteColumn(UUID columnId) {
        super();
        this.columnId = columnId;
    }
    
    // Constructeur avec ID
    public DeleteColumn(UUID id, UUID columnId) {
        super(id);
        this.columnId = columnId;
    }
    
    // Getters
    public UUID getColumnId() {
        return columnId;
    }
    
    // Setters
    public void setColumnId(UUID columnId) {
        this.columnId = columnId;
    }
    
    @Override
    public boolean execute() {
        // Logique pour exécuter la suppression de colonne
        // À implémenter selon les règles métier
        return columnId != null;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la suppression de colonne
        // À implémenter selon les règles métier
        return columnId != null;
    }
    
    @Override
    public String toString() {
        return "DeleteColumn{" +
                "id=" + getId() +
                ", columnId=" + columnId +
                '}';
    }
}
