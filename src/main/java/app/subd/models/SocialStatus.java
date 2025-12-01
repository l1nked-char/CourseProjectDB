package app.subd.models;

public class SocialStatus {
    private int id;
    private String name;

    public SocialStatus() {
        this.id = 0;
        this.name = "";
    }

    public SocialStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

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
        SocialStatus ss = (SocialStatus) obj;
        return id == ss.id;
    }
}
