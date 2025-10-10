package app.subd.models;

public class TypeOfRoom {
    private final Integer room_type_id;
    private final String room_type_name;

    public TypeOfRoom(Integer room_type_id, String room_type_name)
    {
        this.room_type_id = room_type_id;
        this.room_type_name = room_type_name;
    }

    public Integer getId() { return this.room_type_id; }
    public String getName() { return this.room_type_name; }
}
