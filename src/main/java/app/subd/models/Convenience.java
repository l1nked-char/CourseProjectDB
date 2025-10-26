package app.subd.models;

public class Convenience {
    private int id;
    private String name;

    // Конструктор по умолчанию
    public Convenience() {
        this.id = 0;
        this.name = "";
    }

    public Convenience(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Convenience conv = (Convenience) obj;
        return id == conv.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}