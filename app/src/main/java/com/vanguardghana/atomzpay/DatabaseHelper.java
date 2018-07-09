package com.vanguardghana.atomzpay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.utills.DataFactory;
import com.vanguardghana.atomzpay.TollPaymentFactory;
import com.utills.Farmer;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "tollPayment_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table

        db.execSQL(TollPaymentFactory.CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed

        db.execSQL("DROP TABLE IF EXISTS " + TollPaymentFactory.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }


    // insert Data from payment instance
    public long insertPaymentAppData(String appData) {
//        DataFactory df = getAppData(dataId);
//        if (df != null) {
//            return updateAppData(dataId, appData);
//        }
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(TollPaymentFactory.COLUMN_JSON_DATA, appData);
        // insert row
        long id = db.insert(TollPaymentFactory.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        System.out.println("This is the newly inserted row id" + id + "============================>");
        return id;

    }


    public TollPaymentFactory getPaymentAppData(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TollPaymentFactory.TABLE_NAME,
                new String[]{TollPaymentFactory.COLUMN_ID,  TollPaymentFactory.COLUMN_JSON_DATA, TollPaymentFactory.COLUMN_TIMESTAMP},
                TollPaymentFactory.COLUMN_ID + "=?  limit 15",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.getCount() <= 0) {
            return null;
        }
        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        TollPaymentFactory df = new TollPaymentFactory();
        df.setId(cursor.getInt(cursor.getColumnIndex(TollPaymentFactory.COLUMN_ID)));
        df.setJsonData(cursor.getString(cursor.getColumnIndex(TollPaymentFactory.COLUMN_JSON_DATA)));
        df.setTimestamp(cursor.getString(cursor.getColumnIndex(TollPaymentFactory.COLUMN_TIMESTAMP)));

        df.getId();

        // close the db connection
        cursor.close();
        db.close();
        return df;
    }

    public List<TollPaymentFactory> getAllPayments() {
        List<TollPaymentFactory> payments = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TollPaymentFactory.TABLE_NAME + " ORDER BY " +
                TollPaymentFactory.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TollPaymentFactory payment = new TollPaymentFactory();
                payment.setId(cursor.getInt(cursor.getColumnIndex(payment.COLUMN_ID)));
                payment.setJsonData(cursor.getString(cursor.getColumnIndex(payment.COLUMN_JSON_DATA)));
                payment.setTimestamp(cursor.getString(cursor.getColumnIndex(payment.COLUMN_TIMESTAMP)));

                payments.add(payment);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list

        return payments;
    }

    public int getPaymentsCount() {
        String countQuery = "SELECT  * FROM " + TollPaymentFactory.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        db.close();
        // return count
        return count;
    }


    public void deletePayment(TollPaymentFactory tollPaymentFactory) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TollPaymentFactory.TABLE_NAME, TollPaymentFactory.COLUMN_ID + " = ?",
                new String[]{String.valueOf(tollPaymentFactory.getId())});
        db.close();
    }

    public void deleteAllPayment()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TollPaymentFactory.TABLE_NAME);
        db.execSQL(TollPaymentFactory.CREATE_TABLE);
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
        Cursor dbCursor = db.query(TollPaymentFactory.TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            System.out.println("Column Name =>" + columnNames[i]);
        }
    }

}
