package com.example.pc.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.inventoryapp.data.RetailContract;
import com.example.pc.inventoryapp.data.RetailDbHelper;

import java.lang.reflect.Method;

public class AddingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private Uri curProductUri;
    Intent intent;
    EditText nameEdit;
    EditText priceEdit;
    TextView quantityEdit;
    EditText supNameEdit;
    EditText supPhoneEdit;
    Button minusButton;
    Button plusButton;
    String nameStr;
    String priceStr;
    String quantityStr;
    Button contactSupBtn;
    boolean b = true;
    public final String[] PRODUCT_COLUMN = {
            RetailContract.RetailEntry._ID,
            RetailContract.RetailEntry.COLUMN_PRODUCT_NAME,
            RetailContract.RetailEntry.COLUMN_PRODUCT_QUANTITY,
            RetailContract.RetailEntry.COLUMN_PRODUCT_PRICE,
            RetailContract.RetailEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
            RetailContract.RetailEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_activity);
        intent = getIntent();
        curProductUri = intent.getData();
        nameEdit = (EditText) findViewById(R.id.name_edittext);
        priceEdit = (EditText) findViewById(R.id.price_edittext);
        quantityEdit = (TextView) findViewById(R.id.quantity_textview);
        supNameEdit = (EditText) findViewById(R.id.sup_name_edittext);
        supPhoneEdit = (EditText) findViewById(R.id.sup_phone_edittext);
        minusButton = (Button) findViewById(R.id.minus_button);
        plusButton = (Button) findViewById(R.id.plus_button);
        contactSupBtn = (Button) findViewById(R.id.contact_sup_btn);
        if (curProductUri == null) {
            setTitle(getString(R.string.add_product));
        } else {
            setTitle(getString(R.string.edit_product));
            contactSupBtn.setVisibility(View.VISIBLE);
            contactSupBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    contactSupplier();
                }
            });
            getSupportLoaderManager().initLoader(0, null, this);
        }
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityStr = quantityEdit.getText().toString().trim();
                int quantity = Integer.parseInt(quantityStr);
                if (quantity > 0)
                    --quantity;
                String quantityStrAfter = Integer.toString(quantity);
                quantityEdit.setText(quantityStrAfter);
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityStr = quantityEdit.getText().toString().trim();
                int quantity = Integer.parseInt(quantityStr);
                ++quantity;
                String quantityStrAfter = Integer.toString(quantity);
                quantityEdit.setText(quantityStrAfter);
            }
        });
    }

    private void insertProduct() {
        ContentValues vals = new ContentValues();
        nameStr = nameEdit.getText().toString();
        priceStr = priceEdit.getText().toString();
        quantityStr = quantityEdit.getText().toString();
        String supNameStr = supNameEdit.getText().toString();
        String supPhoneStr = supPhoneEdit.getText().toString();
        try {
            int price = Integer.parseInt(priceStr);
            vals.put(RetailContract.RetailEntry.COLUMN_PRODUCT_PRICE, price);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Unaccpeted price value", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantity = Integer.parseInt(quantityStr);
        if (TextUtils.isEmpty(nameStr) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(supNameStr)
                || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(supPhoneStr)) {
            Toast.makeText(this, "Missing fields", Toast.LENGTH_SHORT).show();
            return;
        }
        vals.put(RetailContract.RetailEntry.COLUMN_PRODUCT_NAME, nameStr);
        vals.put(RetailContract.RetailEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        vals.put(RetailContract.RetailEntry.COLUMN_PRODUCT_PRICE, priceStr);
        vals.put(RetailContract.RetailEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supNameStr);
        vals.put(RetailContract.RetailEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supPhoneStr);
        if (curProductUri == null) {
            Uri insertedRow = getContentResolver().insert(RetailContract.RetailEntry.CONTENT_URI, vals);
            if (insertedRow == null) {
                Toast.makeText(this, "Error Inserting", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Done Inserting", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            int rowUpdated = getContentResolver().update(curProductUri, vals, null, null);
            if (rowUpdated == 0) {
                Toast.makeText(this, "Error Updating", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Done Updating", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }

        }
    }


    private void deleteProduct() {
        if (curProductUri != null) {
            int rowsDeleted = getContentResolver().delete(curProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Deletion failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Deletion Done",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showDelConfirmation() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setMessage(R.string.delete_msg);
        build.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        build.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = build.create();
        alertDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void contactSupplier() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supPhoneEdit.getText().toString())));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                insertProduct();
               // finish();

                return true;
            case R.id.delete:
                showDelConfirmation();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this,
                curProductUri,
                PRODUCT_COLUMN,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int c_ID = 0;
            int c_COLUMN_NAME = 1;
            int c_COLUMN_PRICE = 3;
            int c_COLUMN_QUANTITY = 2;
            int c_COLUMN_SUPPLIER_NAME = 4;
            int c_COLUMN_SUPPLIER_PHONE = 5;
            String name = data.getString(c_COLUMN_NAME);
            int quantity = data.getInt(c_COLUMN_QUANTITY);
            int price = data.getInt(c_COLUMN_PRICE);
            String supName = data.getString(c_COLUMN_SUPPLIER_NAME);
            String supPhone = data.getString(c_COLUMN_SUPPLIER_PHONE);
            nameEdit.setText(name);
            priceEdit.setText(String.valueOf(price));
            quantityEdit.setText(String.valueOf(quantity));
            supNameEdit.setText(supName);
            supPhoneEdit.setText(supPhone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        nameEdit.setText("");
        priceEdit.setText(String.valueOf(""));
        quantityEdit.setText(String.valueOf(""));
        supNameEdit.setText("");
        supPhoneEdit.setText("");
    }
}