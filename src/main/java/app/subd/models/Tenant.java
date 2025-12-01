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
    public String socialStatus;
    public Integer series;
    public Integer number;
    public DocumentType documentType;
    public String email;
    public int hotelId;

    public Tenant() {
        this.id = 0;
        this.firstName = "";
        this.name = "";
        this.patronymic = "";
        this.cityId = 0;
        this.birthDate = null;
        this.socialStatusId = 0;
        this.socialStatus = "";
        this.series = 0;
        this.number = 0;
        this.documentType = null;
        this.email = "";
        this.hotelId = 0;
    }

    public Tenant(int id, String firstName, String name, String patronymic, int cityId, int socialStatusId, Integer series, Integer number, DocumentType document_type, String email) {

        this.id = id;
        this.firstName = firstName;
        this.name = name;
        this.patronymic = patronymic;
        this.cityId = cityId;
        this.socialStatusId = socialStatusId;
        this.socialStatus = "";
        this.documentType = document_type;
        if (documentType != null) {
            this.series = series;
            this.number = number;
        }
        this.email = email;
        this.hotelId = 0;
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
    public String getSocialStatus() { return socialStatus; }
    public void setSocialStatus(String socialStatus) { this.socialStatus = socialStatus; }
    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }
    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
    public String getDocumentType() { return documentType != null ? documentType.getDescription() : ""; }
    public void setDocumentType(String documentType) { this.documentType = DocumentType.getDocumentType(documentType); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getHotelId() { return hotelId; }
    public void setHotelId(int hotelId) { this.hotelId = hotelId; }

    public String getPassport() {
        if (documentType == null || documentType == DocumentType.NOT_SPECIFIED) {
            series = null;
            number = null;
            return "";
        }
        return String.format("%04d %06d", series, number);
    }

    @Override
    public String toString() {
        return firstName + " " + name + " " + patronymic + " " + birthDate + " " + email;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tenant tenant = (Tenant) obj;
        return id == tenant.id;
    }
}
