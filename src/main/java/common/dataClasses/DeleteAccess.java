package common.dataClasses;

import java.util.UUID;

public class DeleteAccess extends Modification {
    UUID access;
    Access previousAccess = null;

    // Constructeur
    public DeleteAccess(UUID access) {
        super();
        this.access = access;
    }

    // Getters
    public UUID getAccess() {
        return access;
    }
    public Access getPreviousAccess() {
        return previousAccess;
    }

    // Setters
    public void setAccess(UUID access) {
        this.access = access;
    }
    public void setPreviousAccess(Access previousAccess) {
        this.previousAccess = previousAccess;
    }

    @Override
    public Kanban execute(Kanban targetKanban) {
        LightKanban lightKanban = targetKanban.getLightKanban();
        this.previousAccess = lightKanban.getAccessList().stream()
                .filter(a -> a.getId().equals(access))
                .findFirst()
                .orElse(null);
        lightKanban.getAccessList().removeIf(a -> a.getId().equals(access));
        return targetKanban;
    }

    @Override
    public Kanban undo(Kanban targetKanban) {
        if (previousAccess == null) {
            throw new IllegalStateException("Cannot undo DeleteAccess: previousAccess is null.");
        }
        AddAccess undoModification = new AddAccess(previousAccess);
        return undoModification.execute(targetKanban);
    }

    @Override
    public String toString() {
        return "DeleteAccess{" +
                "id=" + getId() +
                ", access=" + access +
                '}';
    }
}
