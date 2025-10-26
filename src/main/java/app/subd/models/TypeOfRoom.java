package app.subd.models;

public class TypeOfRoom {
    private Integer room_type_id;
    private String room_type_name;

    // Конструктор по умолчанию
    public TypeOfRoom() {
        this.room_type_id = 0;
        this.room_type_name = "";
    }

    public TypeOfRoom(Integer room_type_id, String room_type_name) {
        this.room_type_id = room_type_id;
        this.room_type_name = room_type_name;
    }

    // Геттеры и сеттеры
    public Integer getId() { return this.room_type_id; }
    public void setId(Integer id) { this.room_type_id = id; }

    public String getName() { return this.room_type_name; }
    public void setName(String name) { this.room_type_name = name; }

    @Override
    public String toString() {
        return room_type_name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TypeOfRoom type = (TypeOfRoom) obj;
        return room_type_id.equals(type.room_type_id);
    }

    @Override
    public int hashCode() {
        return room_type_id;
    }
}