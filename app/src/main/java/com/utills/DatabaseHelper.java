package com.utills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "farmers_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Farmer.CREATE_TABLE);
        db.execSQL(DataFactory.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Farmer.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataFactory.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public long insertFarmer(String farmer) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Farmer.COLUMN_FARMER, farmer);

        // insert row
        long id = db.insert(Farmer.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public long insertAppData(int dataId, String appData) {
        DataFactory df = getAppData(dataId);
        if (df != null) {
            return updateAppData(dataId, appData);
        }
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(DataFactory.COLUMN_DATA_ID, dataId);
        values.put(DataFactory.COLUMN_JSON_DATA, appData);
        // insert row
        long id = db.insert(DataFactory.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Farmer getFarmer(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Farmer.TABLE_NAME,
                new String[]{Farmer.COLUMN_ID, Farmer.COLUMN_FARMER, Farmer.COLUMN_TIMESTAMP},
                Farmer.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Farmer farmer = new Farmer(
                cursor.getInt(cursor.getColumnIndex(Farmer.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Farmer.COLUMN_FARMER)),
                cursor.getString(cursor.getColumnIndex(Farmer.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();
        db.close();
        return farmer;
    }

    public DataFactory getAppData(long dataId) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DataFactory.TABLE_NAME,
                new String[]{DataFactory.COLUMN_ID, DataFactory.COLUMN_DATA_ID, DataFactory.COLUMN_JSON_DATA, DataFactory.COLUMN_TIMESTAMP},
                DataFactory.COLUMN_DATA_ID + "=?",
                new String[]{String.valueOf(dataId)}, null, null, null, null);
        if (cursor.getCount() <= 0) {
            return null;
        }
        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        DataFactory df = new DataFactory();
        df.setId(cursor.getInt(cursor.getColumnIndex(DataFactory.COLUMN_ID)));
        df.setDataId(cursor.getInt(cursor.getColumnIndex(DataFactory.COLUMN_DATA_ID)));
        df.setJsonData(cursor.getString(cursor.getColumnIndex(DataFactory.COLUMN_JSON_DATA)));
        df.setTimestamp(cursor.getString(cursor.getColumnIndex(Farmer.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();
        db.close();
        return df;
    }

    public List<Farmer> getAllFarmers() {
        List<Farmer> farmers = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Farmer.TABLE_NAME + " ORDER BY " +
                Farmer.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Farmer farmer = new Farmer();
                farmer.setId(cursor.getInt(cursor.getColumnIndex(farmer.COLUMN_ID)));
                farmer.setFarmer(cursor.getString(cursor.getColumnIndex(farmer.COLUMN_FARMER)));
                farmer.setTimestamp(cursor.getString(cursor.getColumnIndex(farmer.COLUMN_TIMESTAMP)));

                farmers.add(farmer);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return farmers;
    }

    public int getFarmersCount() {
        String countQuery = "SELECT  * FROM " + Farmer.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        db.close();
        // return count
        return count;
    }

    public int updateFarmer(Farmer farmer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(farmer.COLUMN_FARMER, farmer.getFarmer());

        // updating row
        int updateRow = db.update(Farmer.TABLE_NAME, values, Farmer.COLUMN_ID + " = ?",
                new String[]{String.valueOf(farmer.getId())});
        db.close();
        return updateRow;
    }

    public int updateAppData(long dataId, String jsonData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataFactory.COLUMN_JSON_DATA, jsonData);

        // updating row
        int updatedRow = db.update(DataFactory.TABLE_NAME, values, DataFactory.COLUMN_DATA_ID + " = ?",
                new String[]{String.valueOf(dataId)});
        db.close();
        return updatedRow;
    }

    public void deleteFarmer(Farmer farmer) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Farmer.TABLE_NAME, Farmer.COLUMN_ID + " = ?",
                new String[]{String.valueOf(farmer.getId())});
        db.close();
    }

    public boolean deleteFarmerById(String farmerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Farmer.TABLE_NAME, Farmer.COLUMN_ID + " = ?",
                new String[]{farmerId}) > 0;
        //db.close();
    }

    public void deleteFarmerRecord(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Farmer.TABLE_NAME + " WHERE " + Farmer.COLUMN_ID + "='" + value + "'");
        //db.close();
    }

    public void showColumns() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor dbCursor = db.query(Farmer.TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            System.out.println("Column Name =>" + columnNames[i]);
        }
    }

    public int getFarmerCount() {
        String countQuery = "SELECT  * FROM " + Farmer.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        db.close();
        return cnt;
    }
}