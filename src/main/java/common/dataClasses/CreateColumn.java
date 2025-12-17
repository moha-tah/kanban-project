package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class CreateColumn extends Modification {
    private Column newColumn;
    private UUID previousColumnId = null;
    
    // Constructeur
    public CreateColumn(Column newColumn) {
        super();
        this.newColumn = newColumn;
    }
    
    // Constructeur avec kanban cible
    public CreateColumn(Column newColumn, LightKanban targetKanban) {
        super();
        this.newColumn = newColumn;
        this.setLightTargetKanban(targetKanban);
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
    public UUID getPreviousColumnId() {
        return previousColumnId;
    }
    // Setters
    public void setNewColumn(Column newColumn) {
        this.newColumn = newColumn;
    }
    public void setPreviousColumnId(UUID previousColumnId) {
        this.previousColumnId = previousColumnId;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        List<Column> columns = targetKanban.getColumns();
        this.previousColumnId = newColumn.getId();
        columns.add(newColumn);
        // targetKanban.setColumns(columns);
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        DeleteColumn undoModification = new DeleteColumn(previousColumnId);
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "CreateColumn{" +
                "id=" + getId() +
                ", newColumn=" + (newColumn != null ? newColumn.getTitle() : "null") +
                '}';
    }
}
