package com.example.pc.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.inventoryapp.data.RetailContract;
import com.example.pc.inventoryapp.data.RetailDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private RetailCursorAdapter cursorAdapter;
    ListView RetailListView;

    public final String[] PRODUCT_COLUMN = {
            RetailContract.RetailEntry._ID,
            RetailContract.RetailEntry.COLUMN_PRODUCT_NAME,
            RetailContract.RetailEntry.COLUMN_PRODUCT_QUANTITY,
            RetailContract.RetailEntry.COLUMN_PRODUCT_PRICE,
            RetailContract.RetailEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
            RetailContract.RetailEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddingActivity.class);
                startActivity(intent);
            }
        });
        RetailListView = (ListView) findViewById(R.id.listview);
        View emptyView = findViewById(R.id.empty);
        RetailListView.setEmptyView(emptyView);
        RetailCursorAdapter cursorAdapter = new RetailCursorAdapter(this, null);
        RetailListView.setAdapter(cursorAdapter);
        RetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddingActivity.class);

                Uri curProductUri = ContentUris.withAppendedId(RetailContract.RetailEntry.CONTENT_URI, id);
                intent.setData(curProductUri);
                startActivity(intent);

            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                delEntireProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void delEntireProducts() {
        Integer rowsDeleted = getContentResolver().delete(RetailContract.RetailEntry.CONTENT_URI, null, null);
        String rows = String.valueOf(rowsDeleted);
        Toast.makeText(this, "All products are deleted , number of rows deleted: " + rows, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        return new CursorLoader(this,
                RetailContract.RetailEntry.CONTENT_URI,
                PRODUCT_COLUMN,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursorAdapter = new RetailCursorAdapter(this, data);
        RetailListView.setAdapter(cursorAdapter);
        cursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}