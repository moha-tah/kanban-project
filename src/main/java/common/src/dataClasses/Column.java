package common.src.dataClasses;
import java.util.UUID;

public class Column {
    private UUID id;
    private String title;
    private String color;
    private int number;
    
    // Constructeur
    public Column(String title, String color, int number) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.color = color;
        this.number = number;
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getColor() {
        return color;
    }
    
    public int getNumber() {
        return number;
    }
    
    // Setters
    public void setId(UUID id) {
        this.id = id;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
}
