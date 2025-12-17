package common.dataClasses;
import java.util.List;
import java.util.UUID;

public class DeleteColumn extends Modification {
    private UUID columnId;
    private Column previousColumn = null;
    
    // Constructeur
    public DeleteColumn(UUID columnId) {
        super();
        this.columnId = columnId;
    }
    
    // Constructeur avec kanban cible
    public DeleteColumn(UUID columnId, LightKanban targetKanban) {
        super();
        this.columnId = columnId;
        this.setLightTargetKanban(targetKanban);
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
    public Column getPreviousColumn() {
        return previousColumn;
    }
    
    // Setters
    public void setColumnId(UUID columnId) {
        this.columnId = columnId;
    }
    public void setPreviousColumn(Column previousColumn) {
        this.previousColumn = previousColumn;
    }
    
    @Override
    public Kanban execute(Kanban targetKanban) {
        List<Column> columnList = targetKanban.getColumns();
        this.previousColumn = columnList.stream()
                .filter(c -> c.getId().equals(columnId))
                .findFirst()
                .orElse(null);
        //supprimer la colonne avec le meme id
        columnList.removeIf(c -> c.getId().equals(columnId));
        targetKanban.setColumns(columnList);
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        CreateColumn undoModification = new CreateColumn(previousColumn);
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "DeleteColumn{" +
                "id=" + getId() +
                ", columnId=" + columnId +
                '}';
    }
}
