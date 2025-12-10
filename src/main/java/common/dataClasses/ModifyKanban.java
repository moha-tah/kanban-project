package common.dataClasses;

public class ModifyKanban extends Modification {
    private String title = null;
    private String visibility = null;
    private User creator = null;

    private String previousTitle = null;
    private String previousVisibility = null;
    private User previousCreator = null;

    
    // Constructeur
    public ModifyKanban(String title, String visibility, User creator) {
        super();
        this.title = title;
        this.visibility = visibility;
        this.creator = creator;
    }

    // Getters
    public String getTitle() {
        return title;
    }
    public String getVisibility() {
        return visibility;
    }
    public User getCreator() {
        return creator;
    }
    public String getPreviousTitle() {
        return previousTitle;
    }
    public String getPreviousVisibility() {
        return previousVisibility;
    }
    public User getPreviousCreator() {
        return previousCreator;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }
    public void setPreviousTitle(String previousTitle) {
        this.previousTitle = previousTitle;
    }
    public void setPreviousVisibility(String previousVisibility) {
        this.previousVisibility = previousVisibility;
    }
    public void setPreviousCreator(User previousCreator) {
        this.previousCreator = previousCreator;
    }

    @Override
    public Kanban execute(Kanban targetKanban) {
        if (title != null) {
            targetKanban.setTitle(title);
            this.previousTitle = targetKanban.getTitle();
        }
        if (visibility != null) {
            targetKanban.setVisibility(visibility);
            this.previousVisibility = targetKanban.getVisibility();
        }
        if (creator != null) {
            targetKanban.setCreator(creator);
            targetKanban.setCreatorId(creator.getId());
            this.previousCreator = targetKanban.getCreator();
        }
        return targetKanban;
    }
    
    @Override
    public Kanban undo(Kanban targetKanban) {
        ModifyKanban undoModification = new ModifyKanban(previousTitle, previousVisibility, previousCreator);
        return undoModification.execute(targetKanban);
    }
    
    @Override
    public String toString() {
        return "ModifyKanban{" +
                "id=" + getId() +
                ", title=" + (title) +
                ", visibility=" + (visibility) +
                ", creator=" + (creator != null ? creator.getUsername() : "null") +
                '}';
    }
}
