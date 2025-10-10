package app.subd.models;

public class Room {
    private final int id;
    private final int hotelId;
    private final int maxPeople;
    private final double pricePerPerson;
    private final int roomNumber;
    private final Integer typeOfRoomId;
    private final String hotelInfo;
    private final String typeOfRoomName;

    public Room(int id, int hotelId, int maxPeople, double pricePerPerson,
                int roomNumber, Integer typeOfRoomId,
                String hotelInfo, String typeOfRoomName) {
        this.id = id;
        this.hotelId = hotelId;
        this.maxPeople = maxPeople;
        this.pricePerPerson = pricePerPerson;
        this.roomNumber = roomNumber;
        this.typeOfRoomId = typeOfRoomId;
        this.hotelInfo = hotelInfo;
        this.typeOfRoomName = typeOfRoomName;
    }

    // Геттеры
    public int getId() { return id; }
    public int getHotelId() { return hotelId; }
    public int getMaxPeople() { return maxPeople; }
    public double getPricePerPerson() { return pricePerPerson; }
    public int getRoomNumber() { return roomNumber; }
    public Integer getTypeOfRoomId() { return typeOfRoomId; }
    public String getHotelInfo() { return hotelInfo; }
    public String getTypeOfRoomName() { return typeOfRoomName; }
}