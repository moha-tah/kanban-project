package common.dataClasses;

import java.io.Serializable;
import java.util.UUID;

public class Column implements Serializable {

    private static final long serialVersionUID = 1L;

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
    public Column(String title, String color) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.color = color;
    }

    // Getters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getColor() { return color; }
    public int getNumber() { return number; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setColor(String color) { this.color = color; }
    public void setNumber(int number) { this.number = number; }

    // Override equals and hashCode for proper HashMap key behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return id != null ? id.equals(column.id) : column.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}