package app.subd.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HotelService {
    public int id;
    public int hotelId;
    public int serviceNameId;
    public LocalDate startOfPeriod;
    public LocalDate endOfPeriod;
    public BigDecimal pricePerOne;
    public boolean canBeBooked;
    public String serviceName;

    public HotelService() {
        this.id = 0;
        this.hotelId = 0;
        this.serviceNameId = 0;
        this.startOfPeriod = LocalDate.now();
        this.endOfPeriod = LocalDate.now();
        this.pricePerOne = new BigDecimal(0);
        this.canBeBooked = false;
        this.serviceName = "";
    }

    public HotelService(int id, int hotelId, int serviceNameId,  LocalDate startOfPeriod, LocalDate endOfPeriod, BigDecimal pricePerOne, boolean canBeBooked, String serviceName) {
        this.id = id;
        this.hotelId = hotelId;
        this.serviceNameId = serviceNameId;
        this.startOfPeriod = startOfPeriod;
        this.endOfPeriod = endOfPeriod;
        this.pricePerOne = pricePerOne;
        this.canBeBooked = canBeBooked;
        this.serviceName = serviceName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }
    public int getServiceNameId() { return serviceNameId; }
    public void setServiceNameId(int serviceNameId) { this.serviceNameId = serviceNameId; }
    public LocalDate getStartOfPeriod() { return startOfPeriod; }
    public void setStartOfPeriod(LocalDate startOfPeriod) { this.startOfPeriod = startOfPeriod; }
    public LocalDate getEndOfPeriod() { return endOfPeriod; }
    public void setEndOfPeriod(LocalDate endOfPeriod) { this.endOfPeriod = endOfPeriod; }
    public BigDecimal getPricePerOne() { return pricePerOne; }
    public void setPricePerOne(BigDecimal pricePerOne) { this.pricePerOne = pricePerOne; }
    public boolean getCanBeBooked() { return canBeBooked; }
    public void setCanBeBooked(boolean canBeBooked) { this.canBeBooked = canBeBooked; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    @Override
    public String toString() {return serviceName;}
}
