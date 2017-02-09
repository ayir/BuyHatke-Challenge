package com.example.riyasharma.sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RiyaSharma on 07-02-2017.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "SMS.db";
    private static final int DATABASE_VERSION = 1;
    public static final String SMS_TABLE_NAME = "SMS";
    public static final String SMS_COLUMN_ID = "_id";
    public static final String SMS_COLUMN_THREAD = "thread";
    public static final String SMS_COLUMN_ADDRESS = "address";
    public static final String SMS_COLUMN_DATE = "date";
    public static final String SMS_COLUMN_BODY= "body";
    public static final String SMS_COLUMN_TYPE= "type";
    public SQLiteDatabase db=null;
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + SMS_TABLE_NAME + "(" +
                        SMS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SMS_COLUMN_THREAD +" TEXT, "+
                        SMS_COLUMN_ADDRESS + " TEXT, " +
                        SMS_COLUMN_DATE + " TEXT, " +
                        SMS_COLUMN_BODY + " TEXT," +
                        SMS_COLUMN_TYPE + " TEXT)"
        );
        setDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE_NAME);
        onCreate(db);
    }

    public void setDatabase(SQLiteDatabase db){
        this.db=db;
    }
    public SQLiteDatabase getDatabase(){
        return db;
    }
    public boolean insertSMS(String thread_id, String address, String date,String body,String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SMS_COLUMN_ID, thread_id);
        contentValues.put(SMS_COLUMN_ADDRESS,address);
        contentValues.put(SMS_COLUMN_DATE, date);
        contentValues.put(SMS_COLUMN_BODY,body);
        contentValues.put(SMS_COLUMN_TYPE, type);
        db.insertWithOnConflict(SMS_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return true;
    }


}
