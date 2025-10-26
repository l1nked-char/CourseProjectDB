package app.subd.models;

public class City {
    private int cityId;
    private String cityName;

    // Конструктор по умолчанию
    public City() {
        this.cityId = 0;
        this.cityName = "";
    }

    public City(int cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    // Геттеры и сеттеры
    public int getCityId() { return cityId; }
    public void setCityId(int cityId) { this.cityId = cityId; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    @Override
    public String toString() {
        return cityName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        City city = (City) obj;
        return cityId == city.cityId;
    }

    @Override
    public int hashCode() {
        return cityId;
    }
}