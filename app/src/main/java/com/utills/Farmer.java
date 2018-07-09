package com.utills;

public class Farmer {
    public static final String TABLE_NAME = "farmers";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FARMER = "farmer";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String farmer;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_FARMER + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Farmer() {
    }

    public Farmer(int id, String farmerInfo, String timestamp) {
        this.id = id;
        this.farmer = farmerInfo;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFarmer() {
        return farmer;
    }

    public void setFarmer(String farmer) {
        this.farmer = farmer;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
