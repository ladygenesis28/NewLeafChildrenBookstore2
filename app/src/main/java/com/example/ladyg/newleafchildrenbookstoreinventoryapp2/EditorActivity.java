package com.example.ladyg.newleafchildrenbookstoreinventoryapp2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ladyg.newleafchildrenbookstoreinventoryapp2.Data.NewLeafContract;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    //Identifier for the book data loader
    private static final int EXISTING_BOOK_LOADER = 0;


    //Content URI for the existing book (null if it's a new book)
    private Uri mCurrentBookUri;


    //EditText field to enter the book name
    private EditText mNameEditText;

    //EditText field to enter the book price
    private EditText mPriceEditText;

    //EditText field to enter the book quantity
    private EditText mQuantityEditText;

    // EditText field to enter the supplier's name
    private EditText mSupplierNameEditText;

    //EditText field to enter the supplier's phone number
    private EditText mSupplierPhoneNumberEditText;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };
    private int mQuantity;
    private View mQuantityIncreaseButton;
    private View mQuantityDecreaseButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a book"
            setTitle(getString(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.editor_activity_title_new_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Book"
            setTitle(getString(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.editor_activity_title_edit_book));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneNumberEditText = findViewById(R.id.edit_supplier_phone_number);
        mQuantityIncreaseButton = findViewById(R.id.button_increase);
        mQuantityDecreaseButton = findViewById(R.id.button_decrease);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        mQuantityIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityIncrease(v);
            }
        });

        mQuantityDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityDecrease(v);
            }
        });

        Button callSupplierButton = findViewById(R.id.activity_editor_action_button);
        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPhoneNumber = mSupplierPhoneNumberEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", currentPhoneNumber, null));
                startActivity(intent);

            }
        });
    }

    /**
     * Get user input from editor and save book into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String suppliernameString = mSupplierNameEditText.getText().toString().trim();
        String supplierphonenumberString = mSupplierPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (
                TextUtils.isEmpty(nameString)
                        || TextUtils.isEmpty(priceString)
                        || TextUtils.isEmpty(quantityString)
                        || TextUtils.isEmpty(suppliernameString)
                        || TextUtils.isEmpty(supplierphonenumberString)) {
            Toast.makeText(this, getString(R.string.editor_activity_error_while_empty_editor), Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(NewLeafContract.NewLeafEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(NewLeafContract.NewLeafEntry.COLUMN_PRICE, priceString);
        values.put(NewLeafContract.NewLeafEntry.COLUMN_QUANTITY, quantityString);
        values.put(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_NAME, suppliernameString);
        values.put(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierphonenumberString);
        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.

                int priceInteger = Integer.parseInt(priceString);
                if (priceInteger < 0) {
                    return;
                }

                int quantityInteger = Integer.parseInt(quantityString);
                if (quantityInteger < 0) {
                    return;
                }

                // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
                if (mCurrentBookUri == null) {
                    // This is a NEW book, so insert a new book into the provider,
                    // returning the content URI for the new book.
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
                } else {
                    // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
                    // and pass in the new ContentValues. Pass in null for the selection and selection args
                    // because mCurrentBookUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(this, getString(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.editor_update_book_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast.
                        Toast.makeText(this, getString(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.editor_update_book_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public boolean onCreateOptionsMenu (Menu menu){
                // Inflate the menu options from the res/menu/menu_editor.xml file.
                // This adds menu items to the app bar.
                getMenuInflater().inflate(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.menu.menu_editor, menu);
                return true;
            }

            /**
             * This method is called after invalidateOptionsMenu(), so that the
             * menu can be updated (some menu items can be hidden or made visible).
             */
            @Override
            public boolean onPrepareOptionsMenu (Menu menu){
                super.onPrepareOptionsMenu(menu);
                // If this is a new book, hide the "Delete" menu item.
                if (mCurrentBookUri == null) {
                    MenuItem menuItem = menu.findItem(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.id.action_delete);
                    menuItem.setVisible(false);
                }
                return true;
            }

            @Override
            public boolean onOptionsItemSelected (MenuItem item){
                // User clicked on a menu option in the app bar overflow menu
                switch (item.getItemId()) {
                    // Respond to a click on the "Save" menu option
                    case com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.id.action_save:
                        // Save book to database
                        saveBook();
                        // Exit activity
                        finish();
                        return true;
                    // Respond to a click on the "Delete" menu option
                    case com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.id.action_delete:
                        // Pop up confirmation dialog for deletion
                        showDeleteConfirmationDialog();
                        return true;
                    // Respond to a click on the "Up" arrow button in the app bar
                    case android.R.id.home:
                        // If the book hasn't changed, continue with navigating up to parent activity
                        // which is the {@link CatalogActivity}.
                        if (!mBookHasChanged) {
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            return true;
                        }

                        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                        // Create a click listener to handle the user confirming that
                        // changes should be discarded.
                        DialogInterface.OnClickListener discardButtonClickListener =
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // User clicked "Discard" button, navigate to parent activity.
                                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                    }
                                };

                        // Show a dialog that notifies the user they have unsaved changes
                        showUnsavedChangesDialog(discardButtonClickListener);
                        return true;
                }
                return super.onOptionsItemSelected(item);
            }

            /**
             * This method is called when the back button is pressed.
             */
            @Override
            public void onBackPressed () {
                // If the book hasn't changed, continue with handling back button press
                if (!mBookHasChanged) {
                    super.onBackPressed();
                    return;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, close the current activity.
                                finish();
                            }
                        };

                // Show dialog that there are unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
            }

            @Override
            public Loader<Cursor> onCreateLoader ( int i, Bundle bundle){
                // Since the editor shows all pet attributes, define a projection that contains
                // all columns from the pet table
                String[] projection = {
                        NewLeafContract.NewLeafEntry._ID,
                        NewLeafContract.NewLeafEntry.COLUMN_PRODUCT_NAME,
                        NewLeafContract.NewLeafEntry.COLUMN_PRICE,
                        NewLeafContract.NewLeafEntry.COLUMN_QUANTITY,
                        NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_NAME,
                        NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(this,   // Parent activity context
                        mCurrentBookUri,         // Query the content URI for the current pet
                        projection,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
            }

            @Override
            public void onLoadFinished (Loader < Cursor > loader, Cursor cursor){
                // Bail early if the cursor is null or there is less than 1 row in the cursor
                if (cursor == null || cursor.getCount() < 1) {
                    return;
                }

                // Proceed with moving to the first row of the cursor and reading data from it
                // (This should be the only row in the cursor)
                if (cursor.moveToFirst()) {
                    // Find the columns of pet attributes that we're interested in
                    int nameColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_PRODUCT_NAME);
                    int priceColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_PRICE);
                    int quantityColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_QUANTITY);
                    int suppliernameColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_NAME);
                    int supplierphonenumberColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

                    // Extract out the value from the Cursor for the given column index
                    String name = cursor.getString(nameColumnIndex);
                    int price = cursor.getInt(priceColumnIndex);
                    int quantity = cursor.getInt(quantityColumnIndex);
                    String suppliername = cursor.getString(suppliernameColumnIndex);
                    int supplierphonenumber = cursor.getInt(supplierphonenumberColumnIndex);


                    // Update the views on the screen with the values from the database
                    mNameEditText.setText(name);
                    mPriceEditText.setText(Integer.toString(price));
                    mQuantityEditText.setText(Integer.toString(quantity));
                    mSupplierNameEditText.setText(suppliername);
                    mSupplierPhoneNumberEditText.setText(Integer.toString(supplierphonenumber));

                }
            }

            @Override
            public void onLoaderReset (Loader < Cursor > loader) {
                // If the loader is invalidated, clear out all the data from the input fields.
                mNameEditText.setText("");
                mPriceEditText.setText("");
                mQuantityEditText.setText("");
                mSupplierNameEditText.setText("");
                mSupplierPhoneNumberEditText.setText("");
            }

            /**
             * Show a dialog that warns the user there are unsaved changes that will be lost
             * if they continue leaving the editor.
             *
             * @param discardButtonClickListener is the click listener for what to do when
             *                                   the user confirms they want to discard their changes
             */
            private void showUnsavedChangesDialog (
                    DialogInterface.OnClickListener discardButtonClickListener){
                // Create an AlertDialog.Builder and set the message, and click listeners
                // for the postivie and negative buttons on the dialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.unsaved_changes_dialog_msg);
                builder.setPositiveButton(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.discard, discardButtonClickListener);
                builder.setNegativeButton(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.keep_editing, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Keep editing" button, so dismiss the dialog
                        // and continue editing the book.
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            /**
             * Prompt the user to confirm that they want to delete this book.
             */
            private void showDeleteConfirmationDialog () {
                // Create an AlertDialog.Builder and set the message, and click listeners
                // for the postivie and negative buttons on the dialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.delete_dialog_msg);
                builder.setPositiveButton(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Delete" button, so delete the book.
                        deleteBook();
                    }
                });
                builder.setNegativeButton(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Cancel" button, so dismiss the dialog
                        // and continue editing the book.
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            /**
             * Perform the deletion of the book in the database.
             */
            private void deleteBook () {
                // Only perform the delete if this is an existing book.
                if (mCurrentBookUri != null) {
                    // Call the ContentResolver to delete the book at the given content URI.
                    // Pass in null for the selection and selection args because the mCurrentBookUri
                    // content URI already identifies the book that we want.
                    int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

                    // Show a toast message depending on whether or not the delete was successful.
                    if (rowsDeleted == 0) {
                        // If no rows were deleted, then there was an error with the delete.
                        Toast.makeText(this, getString(com.example.ladyg.newleafchildrenbookstoreinventoryapp2.R.string.editor_delete_book_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the delete was successful and we can display a toast.
                        Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                // Close the activity
                finish();

            }

            private void quantityIncrease (View v){
                if (mQuantity < 15) {
                    mQuantity = mQuantity + 1;
                } else {
                    Toast.makeText(this, getString(R.string.editor_activity_button_quantity_increase), Toast.LENGTH_SHORT).show();
                }
                displayQuantity();

            }


            public void quantityDecrease (View view){
                if (mQuantity > 0) {
                    mQuantity = mQuantity - 1;
                } else {
                    Toast.makeText(this, getString(R.string.editor_activity_quantity_empty_stock), Toast.LENGTH_SHORT).show();
                }
                displayQuantity();

            }

            public void displayQuantity () {
                mQuantityEditText.setText(String.valueOf(mQuantity));
    }
        }

