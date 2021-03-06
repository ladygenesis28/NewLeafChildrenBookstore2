package com.example.ladyg.newleafchildrenbookstoreinventoryapp2.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ladyg.newleafchildrenbookstoreinventoryapp2.Data.NewLeafContract.NewLeafEntry;

public class NewLeafDbHelper extends SQLiteOpenHelper {
    // Name of the database file
    private static final String DATABASE_NAME = "book.db";

    //Database version. If I change the database schema, I must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private String BOOKS_TABLE;

    public NewLeafDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        /**
         * This is called when the database is created for the first time.
         */
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + NewLeafEntry.TABLE_NAME + "("
                + NewLeafEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NewLeafEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + NewLeafEntry.COLUMN_PRICE + " INTEGER DEFAULT 0, "
                + NewLeafEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + NewLeafEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL,"
                + NewLeafEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This onUpgrade is a method where its handle db version changes. 
        // For example: when an app is submit at google play at db version =1
        //then adds some new features which needs database change, upgrade at db version = 2. 
        db.execSQL(BOOKS_TABLE);
        //Create tables again
        onCreate(db);

    }
}

