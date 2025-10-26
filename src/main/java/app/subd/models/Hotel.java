package app.subd.models;

public class Hotel {
    private int id;
    private int cityId;
    private String address;
    private String cityName;

    public Hotel() {
        this.id = 0;
        this.cityId = 0;
        this.address = "";
        this.cityName = "";
    }

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
    public void setId(int id) { this.id = id; }

    public int getCityId() { return cityId; }
    public void setCityId(int cityId) { this.cityId = cityId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    @Override
    public String toString() {
        if (cityName != null && !cityName.isEmpty()) {
            return cityName + " - " + address;
        }
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hotel hotel = (Hotel) obj;
        return id == hotel.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}