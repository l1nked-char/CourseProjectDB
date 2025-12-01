package app.subd.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RoomConvenience {
    private int id;
    private int roomId;
    private int convNameId;
    private BigDecimal pricePerOne;
        private int amount;
        private LocalDate startDate;
        private LocalDate endDate;
        private String convName;
    
        // Конструктор по умолчанию
        public RoomConvenience() {
            this.id = 0;
            this.roomId = 0;
            this.convNameId = 0;
            this.pricePerOne = new BigDecimal(0);
            this.amount = 0;
            this.startDate = LocalDate.now();
            this.endDate = LocalDate.now();
            this.convName = "";
        }
    
        public RoomConvenience(int id, int roomId, int convNameId, BigDecimal pricePerOne, int amount, LocalDate startDate, LocalDate endDate, String convName) {
            this.id = id;
            this.roomId = roomId;
            this.convNameId = convNameId;
            this.pricePerOne = pricePerOne;
            this.amount = amount;
            this.startDate = startDate;
            this.endDate = endDate;
            this.convName = convName;
        }
    
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
    
        public int getRoomId() { return roomId; }
        public void setRoomId(int roomId) { this.roomId = roomId; }
    
        public int getConvNameId() { return convNameId; }
        public void setConvNameId(int convNameId) { this.convNameId = convNameId; }
    
        public BigDecimal getPricePerOne() { return pricePerOne; }
        public void setPricePerOne(BigDecimal pricePerOne) { this.pricePerOne = pricePerOne; }
    
        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
    
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getConvName() { return convName; }
    public void setConvName(String convName) { this.convName = convName; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RoomConvenience rc = (RoomConvenience) obj;
        return id == rc.id;
    }
}