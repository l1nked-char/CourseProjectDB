package app.subd.models;

import java.math.BigDecimal;

public class Room {
    private int id;
    private int hotelId;
    private int maxPeople;
    private BigDecimal pricePerPerson;
    private int roomNumber;
    private Integer typeOfRoomId;
    private String hotelInfo;
    private String typeOfRoomName;

    // Конструктор по умолчанию
    public Room() {
        this.id = 0;
        this.hotelId = 0;
        this.maxPeople = 0;
        this.pricePerPerson = new BigDecimal(0);
        this.roomNumber = 0;
        this.typeOfRoomId = 0;
        this.hotelInfo = "";
        this.typeOfRoomName = "";
    }

    public Room(int id, int hotelId, int roomNumber, int maxPeople, float pricePerPerson, int typeOfRoomId) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.maxPeople = maxPeople;
        this.pricePerPerson = BigDecimal.valueOf(pricePerPerson);
        this.typeOfRoomId = typeOfRoomId;
    }

    public Room(int id, int hotelId, int maxPeople, BigDecimal pricePerPerson,
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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public int getMaxPeople() { return maxPeople; }
    public void setMaxPeople(int maxPeople) { this.maxPeople = maxPeople; }

    public BigDecimal getPricePerPerson() { return pricePerPerson; }
    public void setPricePerPerson(BigDecimal pricePerPerson) { this.pricePerPerson = pricePerPerson; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public Integer getTypeOfRoomId() { return typeOfRoomId; }
    public void setTypeOfRoomId(Integer typeOfRoomId) { this.typeOfRoomId = typeOfRoomId; }

    public String getHotelInfo() { return hotelInfo; }
    public void setHotelInfo(String hotelInfo) { this.hotelInfo = hotelInfo; }

    public String getTypeOfRoomName() { return typeOfRoomName; }
    public void setTypeOfRoomName(String typeOfRoomName) { this.typeOfRoomName = typeOfRoomName; }

    @Override
    public String toString() {
        return String.valueOf(roomNumber);
    }
}