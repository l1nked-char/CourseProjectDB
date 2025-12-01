package app.subd.models;

import java.time.LocalDate;

public class TenantHistory {
    private String bookingNumber;
    private int roomId;
    private int tenantId;
    private LocalDate bookingDate;
    private LocalDate checkInDate;
    private BookingStatus checkInStatus;
    private int occupiedSpace;
    private int amountOfNights;
    private boolean canBeSplit;
    private int hotelId;
    private String tenantInfo;
    private String roomInfo;

    public TenantHistory()
    {
        this.bookingNumber = "";
        this.roomId = 0;
        this.tenantId = 0;
        this.bookingDate = null;
        this.checkInDate = null;
        this.checkInStatus = null;
        this.occupiedSpace = 0;
        this.amountOfNights = 0;
        this.canBeSplit = false;
        this.tenantInfo = "";
        this.roomInfo = "";
    }

    public TenantHistory(String bookingNumber, int roomId, String roomInfo, int tenantId, String tenantInfo, LocalDate bookingDate,
                         LocalDate checkInDate, BookingStatus checkInStatus, int occupiedSpace,
                         int amountOfNights, boolean canBeSplit) {
        this.bookingNumber = bookingNumber;
        this.roomId = roomId;
        this.tenantId = tenantId;
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkInStatus = checkInStatus;
        this.occupiedSpace = occupiedSpace;
        this.amountOfNights = amountOfNights;
        this.canBeSplit = canBeSplit;
        this.tenantInfo = tenantInfo;
        this.roomInfo = roomInfo;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckInStatus() {
        return checkInStatus.getDescription();
    }

    public void setCheckInStatus(BookingStatus checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public int getOccupiedSpace() {
        return occupiedSpace;
    }

    public void setOccupiedSpace(int occupiedSpace) {
        this.occupiedSpace = occupiedSpace;
    }

    public int getAmountOfNights() {
        return amountOfNights;
    }

    public void setAmountOfNights(int amountOfNights) {
        this.amountOfNights = amountOfNights;
    }

    public boolean isCanBeSplit() {
        return canBeSplit;
    }

    public void setCanBeSplit(boolean canBeSplit) {
        this.canBeSplit = canBeSplit;
    }

    public String getTenantInfo() {
        return tenantInfo;
    }

    public void setTenantInfo(String tenantInfo) {
        this.tenantInfo = tenantInfo;
    }

    public String getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(String roomInfo) {
        this.roomInfo = roomInfo;
    }

    @Override
    public String toString() {
        return bookingNumber;
    }
}
