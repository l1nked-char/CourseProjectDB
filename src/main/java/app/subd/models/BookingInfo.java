package app.subd.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingInfo {
    private String bookingNumber;
    private String tenantName;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private BigDecimal totalCost;

    public BookingInfo() {
        this.bookingNumber = "";
        this.tenantName = "";
        this.roomNumber = "";
        this.checkInDate = null;
        this.checkOutDate = null;
        this.status = "";
        this.totalCost = null;
    }

    public BookingInfo(String bookingNumber, String tenantName, String roomNumber, LocalDate checkInDate, LocalDate checkOutDate, String status, BigDecimal totalCost) {
        this.bookingNumber = bookingNumber;
        this.tenantName = tenantName;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.totalCost = totalCost;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}