package app.subd.models;

public class Hotel {
    private final int id;
    private final int cityId;
    private final String address;
    private String cityName; // опционально

    public Hotel(int id, int cityId, String address) {
        this.id = id;
        this.cityId = cityId;
        this.address = address;
    }

    public Hotel(int id, int cityId, String address, String cityName) {
        this.id = id;
        this.cityId = cityId;
        this.address = address;
        this.cityName = cityName;
    }

    public int getId() { return id; }
    public int getCityId() { return cityId; }
    public String getAddress() { return address; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
}