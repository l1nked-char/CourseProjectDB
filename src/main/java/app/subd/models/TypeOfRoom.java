package app.subd.models;

public class TypeOfRoom {
    private Integer id;
    private String name;

    public TypeOfRoom() {
        this.id = 0;
        this.name = "";
    }

    public TypeOfRoom(Integer room_type_id, String room_type_name) {
        this.id = room_type_id;
        this.name = room_type_name;
    }

    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TypeOfRoom type = (TypeOfRoom) obj;
        return id.equals(type.id);
    }
}