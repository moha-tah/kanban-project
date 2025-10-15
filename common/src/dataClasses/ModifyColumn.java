package common.src.dataClasses;
import java.util.UUID;

public class ModifyColumn extends Modification {
    private Column column;
    
    // Constructeur
    public ModifyColumn(Column column) {
        super();
        this.column = column;
    }
    
    // Constructeur avec ID
    public ModifyColumn(UUID id, Column column) {
        super(id);
        this.column = column;
    }
    
    // Getters
    public Column getColumn() {
        return column;
    }
    
    // Setters
    public void setColumn(Column column) {
        this.column = column;
    }
    
    @Override
    public boolean execute() {
        // Logique pour exécuter la modification de colonne
        // À implémenter selon les règles métier
        return column != null;
    }
    
    @Override
    public boolean undo() {
        // Logique pour annuler la modification de colonne
        // À implémenter selon les règles métier
        return column != null;
    }
    
    @Override
    public String toString() {
        return "ModifyColumn{" +
                "id=" + getId() +
                ", column=" + (column != null ? column.getTitle() : "null") +
                '}';
    }
}
