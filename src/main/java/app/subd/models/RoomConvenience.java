package app.subd.models;

import java.time.LocalDate;

public class RoomConvenience {
    private int roomId;
    private int convNameId;
    private double pricePerOne;
    private int amount;
    private LocalDate startDate;
    private String convName;

    // Конструктор по умолчанию
    public RoomConvenience() {
        this.roomId = 0;
        this.convNameId = 0;
        this.pricePerOne = 0.0;
        this.amount = 0;
        this.startDate = LocalDate.now();
        this.convName = "";
    }

    public RoomConvenience(int roomId, int convNameId, double pricePerOne, int amount, LocalDate startDate, String convName) {
        this.roomId = roomId;
        this.convNameId = convNameId;
        this.pricePerOne = pricePerOne;
        this.amount = amount;
        this.startDate = startDate;
        this.convName = convName;
    }

    // Геттеры и сеттеры
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getConvNameId() { return convNameId; }
    public void setConvNameId(int convNameId) { this.convNameId = convNameId; }

    public double getPricePerOne() { return pricePerOne; }
    public void setPricePerOne(double pricePerOne) { this.pricePerOne = pricePerOne; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public String getConvName() { return convName; }
    public void setConvName(String convName) { this.convName = convName; }
}