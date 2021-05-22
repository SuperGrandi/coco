package com.example.coco.lockscreen.util;

import android.database.*;
import android.database.sqlite.*;
import android.content.*;

public class SMSDBhelper{
    private SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;

    private static final String DATABASE_NAME = "smslists.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "ContactList";
    public static final String COLUMN_CONTACT = "contact";
    public static final String _ID = "id";
    private final Context mContext;

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " + COLUMN_CONTACT + " TEXT NOT NULL" + ");";

    public SMSDBhelper(Context context) {
        this.mContext = context;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }

    public SMSDBhelper open() throws SQLException {
        mDBHelper = new SMSDBhelper.DatabaseHelper(mContext);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDBHelper.close();
    }

    public void addNewContact(String contact) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CONTACT, contact);
        mDB.insert(TABLE_NAME, null, cv);
    }

    public void removeContact(String contact) {
        mDB.delete(TABLE_NAME, "contact" + "=?", new String[] {contact});
    }

    public void removeAllContact() {
        mDB.delete(TABLE_NAME, null, null);
    }

    public Cursor getAllContacts() {
        return mDB.query(TABLE_NAME, null, null, null, null, null, COLUMN_CONTACT);
    }
}
