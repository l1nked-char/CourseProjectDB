package app.subd.models;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty id;
    private final StringProperty username;
    private final StringProperty role;
    private final StringProperty hotelInfo;
    private final BooleanProperty userLocked;

    public User() {
        this.id = new SimpleIntegerProperty(0);
        this.username = new SimpleStringProperty("");
        this.role = new SimpleStringProperty("");
        this.hotelInfo = new SimpleStringProperty("");
        this.userLocked = new SimpleBooleanProperty(false);
    }

    public User(int id, String username, String role, String hotelInfo, Boolean userLocked) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.role = new SimpleStringProperty(role);
        this.hotelInfo = new SimpleStringProperty(hotelInfo);
        this.userLocked = new SimpleBooleanProperty(userLocked);
    }

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }
    public StringProperty usernameProperty() { return username; }

    public String getRole() { return role.get(); }
    public void setRole(String value) { role.set(value); }
    public StringProperty roleProperty() { return role; }

    public String getHotelInfo() { return hotelInfo.get(); }
    public void setHotelInfo(String value) { hotelInfo.set(value); }
    public StringProperty hotelInfoProperty() { return hotelInfo; }

    public boolean getUserLocked() { return userLocked.get(); }
    public void setUserLocked(boolean value) { userLocked.set(value); }
    public BooleanProperty userLockedProperty() { return userLocked; }
}