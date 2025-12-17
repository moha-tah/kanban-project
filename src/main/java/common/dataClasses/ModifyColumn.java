package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class ModifyColumn extends Modification {
    private Column column;
    private Column previousColumn = null;
    
    // Constructeur
    public ModifyColumn(Column column) {
        super();
        this.column = column;
    }
    
    // Constructeur avec kanban cible
    public ModifyColumn(Column column, LightKanban targetKanban) {
        super();
        this.column = column;
        this.setLightTargetKanban(targetKanban);
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
    public Column getPreviousColumn() {
        return previousColumn;
    }
    
    // Setters
    public void setColumn(Column column) {
        this.column = column;
    }
    public void setPreviousColumn(Column previousColumn) {
        this.previousColumn = previousColumn;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        List<Column> columnsList = targetKanban.getColumns();
        this.previousColumn = columnsList.stream().filter(c -> c.getId().equals(column.getId())).findFirst().orElse(null);
        columnsList.removeIf(c -> c.getId().equals(column.getId()));
        columnsList.add(column);
        targetKanban.setColumns(columnsList);
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        if (previousColumn == null) {
            throw new IllegalStateException("Cannot undo ModifyColumn: previousColumn is null");
        }
        ModifyColumn undoModification = new ModifyColumn(previousColumn);
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "ModifyColumn{" +
                "id=" + getId() +
                ", column=" + (column != null ? column.getTitle() : "null") +
                '}';
    }
}
