package com.example.pc.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class RetailProvider extends ContentProvider {
    private static final String TAG = RetailProvider.class.getSimpleName();
    RetailDbHelper retailDbHelper;
    private static final int ALL_PRODUCTS = 1;
    private static final int PRODUCTS_BY_ID = 2;
    private static final UriMatcher statUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        statUriMatcher.addURI(RetailContract.CONTENT_AUTHORITY, RetailContract.PATH_PRODUCTS, ALL_PRODUCTS);
        statUriMatcher.addURI(RetailContract.CONTENT_AUTHORITY, RetailContract.PATH_PRODUCTS + "/#", PRODUCTS_BY_ID);
    }


    @Override
    public boolean onCreate() {
        retailDbHelper = new RetailDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        SQLiteDatabase database = retailDbHelper.getReadableDatabase();
        int match = statUriMatcher.match(uri);
        switch (match) {
            case ALL_PRODUCTS:
                cursor = database.query(RetailContract.RetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case PRODUCTS_BY_ID:

                selection = RetailContract.RetailEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(RetailContract.RetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("The Form of the uri is unknown, cannot query " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = statUriMatcher.match(uri);
        if (match == ALL_PRODUCTS) {

            return insertProduct(uri, contentValues);
        } else
            throw new IllegalArgumentException("Cannot insert with that Uri" + uri.toString());

    }

    public Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(RetailContract.RetailEntry.COLUMN_PRODUCT_NAME);
        if (name == null)
            throw new IllegalArgumentException("No product name");
        Integer quantity = values.getAsInteger(RetailContract.RetailEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity < 0)
            throw new IllegalArgumentException("minus quanitiy not accepted");
        Long price = values.getAsLong(RetailContract.RetailEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("product requires valid price");
        }
        SQLiteDatabase database = retailDbHelper.getWritableDatabase();

        long id = database.insert(RetailContract.RetailEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(TAG, "Cannot insert a new row for uri:  " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = retailDbHelper.getWritableDatabase();
        int match = statUriMatcher.match(uri);
        int rowsAfterUpdate;
        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot update, values must not be empty");
        }
        Integer quantity = contentValues.getAsInteger(RetailContract.RetailEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity < 0)
            throw new IllegalArgumentException("minus quanitiy not accepted");
        Long price = contentValues.getAsLong(RetailContract.RetailEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("product requires valid price");
        }
        switch (match) {
            case ALL_PRODUCTS:
                rowsAfterUpdate = database.update(RetailContract.RetailEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case PRODUCTS_BY_ID:
                rowsAfterUpdate = database.update(RetailContract.RetailEntry.TABLE_NAME,
                        contentValues,
                        RetailContract.RetailEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Cannot insert with that Uri " + uri);
        }
        return rowsAfterUpdate;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = retailDbHelper.getWritableDatabase();
        int match = statUriMatcher.match(uri);
        int rowsAfterDeletion;
        switch (match) {
            case ALL_PRODUCTS:
                rowsAfterDeletion = database.delete(RetailContract.RetailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_BY_ID:
                selection = RetailContract.RetailEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsAfterDeletion = database.delete(RetailContract.RetailEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot Delete From that uri " + uri);
        }
        if (rowsAfterDeletion != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAfterDeletion;
    }

    @Override
    public String getType(Uri uri) {
        final int match = statUriMatcher.match(uri);
        switch (match) {
            case ALL_PRODUCTS:
                return RetailContract.RetailEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_BY_ID:
                return RetailContract.RetailEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("uri entered is not known " + uri + " with match " + match);
        }
    }
}