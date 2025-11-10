package app.subd.models;

import java.math.BigDecimal;

public class AvailableRoom {
    private int roomId;
    private int roomNumber;
    private String roomType;
    private int maxPeople;
    private BigDecimal pricePerNight;
    private boolean isAvailable;

    public AvailableRoom() {
        this.roomId = 0;
        this.roomNumber = 0;
        this.roomType = "";
        this.maxPeople = 0;
        this.pricePerNight = BigDecimal.ZERO;
        this.isAvailable = false;
    }

    public AvailableRoom(int roomId, int roomNumber, String roomType, int maxPeople, BigDecimal pricePerNight, boolean isAvailable) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.maxPeople = maxPeople;
        this.pricePerNight = pricePerNight;
        this.isAvailable = isAvailable;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}