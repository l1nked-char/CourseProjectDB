package app.subd.models;

import javafx.beans.property.*;

public class User {
    private int id;
    private String username;
    private String role;
    private String hotelInfo;
    private int hotelId;
    private boolean userLocked;


    private String password;
    private String confirmPassword;
    private String tempPassword;

    public User() {
        this.id = 0;
        this.username = "";
        this.role = "";
        this.hotelInfo = "";
        this.userLocked = false;
        this.password = "";
        this.confirmPassword = "";
        this.tempPassword = "";
    }

    public User(int id, String username, String role, String hotelInfo, Boolean userLocked) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.hotelInfo = hotelInfo;
        this.userLocked = userLocked != null ? userLocked : false;
        this.password = "";
        this.confirmPassword = "";
        this.tempPassword = "";
    }


    public int getId() { return id; }
    public void setId(int value) { this.id = value; }

    public String getUsername() { return username; }
    public void setUsername(String value) { this.username = value; }

    public String getRole() { return role; }
    public void setRole(String value) { this.role = value; }

    public String getHotelInfo() { return hotelInfo; }
    public void setHotelInfo(String value) { this.hotelInfo = value; }

    public boolean getUserLocked() { return userLocked; }
    public void setUserLocked(boolean value) { this.userLocked = value; }

    public String getPassword() { return password; }
    public void setPassword(String value) { this.password = value; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String value) { this.confirmPassword = value; }

    public String getTempPassword() { return tempPassword; }
    public void setTempPassword(String value) { this.tempPassword = value; }

    public int getHotelId() { return hotelId; }
    public void setHotelId(int value) { this.hotelId = value; }
}