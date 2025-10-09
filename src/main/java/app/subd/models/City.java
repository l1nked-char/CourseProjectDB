package app.subd.models;

public class City {
    private final int cityId;
    private final String cityName;

    public City(int cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public int getCityId() { return cityId; }
    public String getCityName() { return cityName; }
}
