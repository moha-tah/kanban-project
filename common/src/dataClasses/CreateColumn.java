package common.src.dataClasses;
import java.util.UUID;

public class CreateColumn extends Modification {
    private Column newColumn;
    
    // Constructeur
    public CreateColumn(Column newColumn) {
        super();
        this.newColumn = newColumn;
    }
    
    // Constructeur avec ID
    public CreateColumn(UUID id, Column newColumn) {
        super(id);
        this.newColumn = newColumn;
    }
    
    // Getters
    public Column getNewColumn() {
        return newColumn;
    }
    
    // Setters
    public void setNewColumn(Column newColumn) {
        this.newColumn = newColumn;
    }
    
    @Override
    public boolean execute() {
        // Logique pour exécuter la création de colonne
        // À implémenter selon les règles métier
        return newColumn != null;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la création de colonne
        // À implémenter selon les règles métier
        return newColumn != null;
    }
    
    @Override
    public String toString() {
        return "CreateColumn{" +
                "id=" + getId() +
                ", newColumn=" + (newColumn != null ? newColumn.getTitle() : "null") +
                '}';
    }
}
