package com.palacios.terceraactividadjaveriana.Classes;

public class User {

    public static boolean isAvailableGlobal=false;
    private String name;
    private String lastName;
    private String id;
    private String email;
    private String password;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private boolean isAvailable;

    private String uuid;

    public User() {
    }

    public User(String name, String lastName, String id, String email, String password, double latitude, double longitude, String imageUrl, boolean isAvailable, String uuid) {
        this.name = name;
        this.lastName = lastName;
        this.id = id;
        this.email = email;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public void setIsAvailable(boolean available) {
        this.isAvailable = available;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
