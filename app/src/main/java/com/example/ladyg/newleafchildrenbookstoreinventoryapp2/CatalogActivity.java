package com.example.ladyg.newleafchildrenbookstoreinventoryapp2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ladyg.newleafchildrenbookstoreinventoryapp2.Data.NewLeafContract;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int NEWLEAF_LOADER = 0;


    private CursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the book data
        ListView newleafListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        newleafListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new NewLeafCursorAdapter(this, null);
        newleafListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        newleafListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link BookEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.books/books/2"
                // if the book with ID 2 was clicked on.
                Uri currentNewLeafUri = ContentUris.withAppendedId(NewLeafContract.NewLeafEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentNewLeafUri);

                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(NEWLEAF_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded book data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        // and book attributes are the values.
        ContentValues values = new ContentValues();
        values.put(NewLeafContract.NewLeafEntry.COLUMN_PRODUCT_NAME, "Adventures Into St. Everston");
        values.put(NewLeafContract.NewLeafEntry.COLUMN_PRICE, "1499");
        values.put(NewLeafContract.NewLeafEntry.COLUMN_QUANTITY, "5");
        values.put(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_NAME, "Erica");
        values.put(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "8003127562");

        // Insert a new row for bookstore1 into the provider using the ContentResolver.
        // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        // Receive the new content URI that will allow us to access Bookstore1 data in the future.
        Uri newUri = getContentResolver().insert(NewLeafContract.NewLeafEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.editor_insert_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.editor_insert_book_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to delete all children books in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(NewLeafContract.NewLeafEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from book database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                NewLeafContract.NewLeafEntry._ID,
                NewLeafContract.NewLeafEntry.COLUMN_PRODUCT_NAME,
                NewLeafContract.NewLeafEntry.COLUMN_PRICE,
                NewLeafContract.NewLeafEntry.COLUMN_QUANTITY,
                NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_NAME,
                NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                NewLeafContract.NewLeafEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link NewLeafCursorAdapter} with this new cursor containing updated book data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);

    }
}
