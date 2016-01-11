package com.example.app.fraapproject.mobile_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Duke on 1/11/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABAE_NAME = "task";
    private static final String TABLE_USER_DETAILS = "user_info";

    // contets of the table
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMAIL_ADRESS = "email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IMAGE_URL = "image_";
    private static final String KEY_SERVER_AUTHCODE = "server_auth";


    public DatabaseHandler(Context context) {
        super(context, DATABAE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_DETAILS + "("
                + KEY_USER_NAME + " VARCHAR(150), " + KEY_EMAIL_ADRESS + " VARCHAR(20), " + KEY_USER_ID + " TEXT ," + KEY_IMAGE_URL + " TEXT, " + KEY_SERVER_AUTHCODE + " INT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
        onCreate(db);
        db.close();
    }


    public void addUserDetails(UserDetails userDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, userDetails.getUserName());
        values.put(KEY_EMAIL_ADRESS, userDetails.getEmail());
        values.put(KEY_USER_ID, userDetails.getUserId());
        values.put(KEY_IMAGE_URL, userDetails.getImagePath());
        values.put(KEY_SERVER_AUTHCODE, userDetails.getAuthCode());
        db.insert(TABLE_USER_DETAILS, null, values);
        db.close();
    }

    public void clearDataFromTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String dataClearCommand = "DELETE FROM " + TABLE_USER_DETAILS;
        db.execSQL(dataClearCommand);
        db.execSQL("vacuum");

    }


    public List<UserDetails> getUserDetails() {

        List<UserDetails> userDetailses = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_USER_DETAILS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                UserDetails userDetails = new UserDetails();
                userDetails.setUserName(cursor.getString(0));
                userDetails.set_userEmail(cursor.getString(1));
                userDetails.setUserId(cursor.getString(2));
                userDetails.setImagePath(cursor.getString(3));
                userDetails.setAuthCode(cursor.getString(4));
                userDetailses.add(userDetails);
            } while (cursor.moveToNext());
        }
        return userDetailses;

    }


}
