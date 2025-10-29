package app.subd.models;

import java.time.LocalDate;

public class Tenant {
    public int id;
    public String firstName;
    public String name;
    public String patronymic;
    public int cityId;
    public LocalDate birthDate;
    public int socialStatusId;
    public int series;
    public int number;
    public String documentType;
    public String email;

    Tenant() {
        this.id = 0;
        this.firstName = "";
        this.name = "";
        this.patronymic = "";
        this.cityId = 0;
        this.birthDate = null;
        this.socialStatusId = 0;
        this.series = 0;
        this.number = 0;
        this.documentType = "";
        this.email = "";
    }

    public Tenant(int id, String firstName, String name, String patronymic, int cityId, int socialStatusId, int series, int number, String document_type, String email) {
        this.id = id;
        this.firstName = firstName;
        this.name = name;
        this.patronymic = patronymic;
        this.cityId = cityId;
        this.socialStatusId = socialStatusId;
        this.series = series;
        this.number = number;
        this.documentType = document_type;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) {  this.patronymic = patronymic; }
    public int getCityId() { return cityId; }
    public void setCityId(int cityId) { this.cityId = cityId; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public int getSocialStatusId() { return socialStatusId; }
    public void setSocialStatusId(int socialStatusId) { this.socialStatusId = socialStatusId; }
    public int getSeries() { return series; }
    public void setSeries(int series) { this.series = series; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return firstName + " " + name + " " + patronymic + " " + birthDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tenant tenant = (Tenant) obj;
        return id == tenant.id;
    }
}
