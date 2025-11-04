package app.subd.models;

import java.time.LocalDate;

public class TenantHistory {
    private String bookingNumber; // вместо id уникальный номер бронирования
    private int roomId;
    private int tenantId;
    private LocalDate bookingDate;
    private LocalDate checkInDate;
    private BookingStatus checkInStatus;
    private int occupiedSpace;
    private int amountOfNights;
    private boolean canBeSplit;
    private int hotelId;

    public TenantHistory() {
        this.bookingNumber = "";
        this.roomId = 0;
        this.tenantId = 0;
        this.bookingDate = null;
        this.checkInDate = null;
        this.checkInStatus = null;
        this.occupiedSpace = 0;
        this.amountOfNights = 0;
        this.canBeSplit = false;
    }

    public TenantHistory(String bookingNumber, int roomId, int tenantId, LocalDate bookingDate, LocalDate checkInDate, BookingStatus checkInStatus, int occupiedSpace, int amountOfNights, boolean canBeSplit) {
        this.bookingNumber = bookingNumber;
        this.roomId = roomId;
        this.tenantId = tenantId;
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkInStatus = checkInStatus;
        this.occupiedSpace = occupiedSpace;
        this.amountOfNights = amountOfNights;
        this.canBeSplit = canBeSplit;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getBookingNumber() { return this.bookingNumber; }
    public void setBookingNumber(String bookingNumber) {this.bookingNumber = bookingNumber;}
    public int getTenantId() { return this.tenantId; }
    public void setTenantId(int tenantId) {this.tenantId = tenantId;}
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public String getCheckInStatus() { return checkInStatus.getDescription(); }
    public void setCheckInStatus(String checkInStatus) { this.checkInStatus = BookingStatus.getBookingStatus(checkInStatus); }
    public int getOccupiedSpace() { return occupiedSpace; }
    public void setOccupiedSpace(int occupiedSpace) { this.occupiedSpace = occupiedSpace; }
    public int getAmountOfNights() { return amountOfNights; }
    public void setAmountOfNights(int amountOfNights) { this.amountOfNights = amountOfNights; }
    public boolean isCanBeSplit() { return canBeSplit; }
    public void setCanBeSplit(boolean canBeSplit) { this.canBeSplit = canBeSplit; }

    @Override
    public String toString() {
        return bookingNumber;
    }
}
