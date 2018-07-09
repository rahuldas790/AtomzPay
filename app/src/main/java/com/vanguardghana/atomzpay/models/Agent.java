package com.vanguardghana.atomzpay.models;

public class Agent {

    private int id;
    private int locationId;
    private String collectorCode, name, email, phoneNumber, locationCode, locationName;

    public Agent() {
        //empty constructor
    }

    public Agent(int id, String collectorCode, String name, String email, String phoneNumber, int locationId, String locationCode, String locationName) {
        this.id = id;
        this.collectorCode = collectorCode;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.locationId = locationId;
        this.locationCode = locationCode;
        this.locationName = locationName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getCollectorCode() {
        return collectorCode;
    }

    public void setCollectorCode(String collectorCode) {
        this.collectorCode = collectorCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

}
