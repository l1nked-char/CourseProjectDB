package app.subd.models;

import java.time.LocalDate;

public class RoomConvenience {
    private final int roomId;
    private final int convNameId;
    private final double pricePerOne;
    private final int amount;
    private final LocalDate startDate;
    private final String convName;

    public RoomConvenience(int roomId, int convNameId, double pricePerOne, int amount, LocalDate startDate, String convName) {
        this.roomId = roomId;
        this.convNameId = convNameId;
        this.pricePerOne = pricePerOne;
        this.amount = amount;
        this.startDate = startDate;
        this.convName = convName;
    }

    // Геттеры
    public int getRoomId() { return roomId; }
    public int getConvNameId() { return convNameId; }
    public double getPricePerOne() { return pricePerOne; }
    public int getAmount() { return amount; }
    public LocalDate getStartDate() { return startDate; }
    public String getConvName() { return convName; }
}