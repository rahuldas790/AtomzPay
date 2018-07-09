package com.vanguardghana.atomzpay;

import com.google.gson.JsonObject;

public class TollPaymentFactory {

    public static final String TABLE_NAME = "payment_data";

    public static final String COLUMN_ID = "id";

    public static final  String COLUMN_JSON_DATA="json_data";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private int dataId;
    private String jsonData;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_JSON_DATA + " TEXT NOT NULL,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public TollPaymentFactory() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
