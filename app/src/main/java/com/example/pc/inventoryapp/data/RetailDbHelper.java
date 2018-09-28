package com.example.pc.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RetailDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stock.db";
    private static final int DATABASE_VER = 2;

    public RetailDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_PRODUCTS_TABLE;
        SQL_PRODUCTS_TABLE = "CREATE TABLE " + RetailContract.RetailEntry.TABLE_NAME + " ("
                + RetailContract.RetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RetailContract.RetailEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + RetailContract.RetailEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + RetailContract.RetailEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + RetailContract.RetailEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL,"
                + RetailContract.RetailEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT NOT NULL);";
        db.execSQL(SQL_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RetailContract.RetailEntry.TABLE_NAME);
        onCreate(db);
    }
}