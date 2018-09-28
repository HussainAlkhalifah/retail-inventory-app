package com.example.pc.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.inventoryapp.data.RetailContract;

public class RetailCursorAdapter extends CursorAdapter {


    public RetailCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.show_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.show_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.show_quantity);
        Button saleButton = (Button) view.findViewById(R.id.sale_btn);
        int _id = cursor.getInt(cursor.getColumnIndex(RetailContract.RetailEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(RetailContract.RetailEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(RetailContract.RetailEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(RetailContract.RetailEntry.COLUMN_PRODUCT_QUANTITY);
        final String productName = cursor.getString(nameColumnIndex);
        Integer price = cursor.getInt(priceColumnIndex);
        final Integer quantity = cursor.getInt(quantityColumnIndex);
        String productPrice = price.toString();
        String productQuantity = quantity.toString();
        final Uri curProductUri = ContentUris.withAppendedId(RetailContract.RetailEntry.CONTENT_URI, _id);
        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues vals = new ContentValues();
                if (quantity > 0) {
                    int qSubtrcat = quantity;
                    --qSubtrcat;
                    vals.put(RetailContract.RetailEntry.COLUMN_PRODUCT_QUANTITY, qSubtrcat);

                    resolver.update(
                            curProductUri,
                            vals,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(curProductUri, null);
                } else {
                    Toast.makeText(context, "Sorry All Sold Out, item is out of stock of " + productName, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}