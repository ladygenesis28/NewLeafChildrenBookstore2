package com.example.ladyg.newleafchildrenbookstoreinventoryapp2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.ladyg.newleafchildrenbookstoreinventoryapp2.Data.NewLeafContract;

/**
 * {@link NewLeafCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class NewLeafCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link NewLeafCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public NewLeafCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the bookdata (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     * correct row.
     */
    private int mQuantity;

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.product_name);
        TextView priceTextView = view.findViewById(R.id.list_item_title_price);
        TextView quantityTextView = view.findViewById(R.id.list_item_title_quantity);
        TextView suppliernameTextView = view.findViewById(R.id.list_item_supplier_name);
        TextView supplierphonenumberTextView = view.findViewById(R.id.list_item_supplier_phone_number);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_QUANTITY);
        int suppliernameColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_NAME);
        int supplierphonenumberColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        // Read the book attributes from the Cursor for the current book
        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        String suppliername = cursor.getString(suppliernameColumnIndex);
        String supplierphonenumber = cursor.getString(supplierphonenumberColumnIndex);

        // If the supplier phone number is empty string or null, then use some default text
        // that says "Unknown phone number of the supplier", so the TextView isn't blank.
        if (TextUtils.isEmpty(supplierphonenumber)) {
            supplierphonenumber = context.getString(R.string.unknown_phone_number);
        }


        // Update the TextViews with the attributes for the current book
        nameTextView.setText(name);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);
        suppliernameTextView.setText(suppliername);
        supplierphonenumberTextView.setText(supplierphonenumber);

        Button buttonSale = view.findViewById(R.id.button_sale);
        mQuantity = Integer.parseInt(quantity);
        buttonSale.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Get the product ID
                    final int idColumnIndex = cursor.getInt(cursor.getColumnIndex(NewLeafContract.NewLeafEntry._ID));
                    //Must validate negative values
                    if (mQuantity > 0)
                        mQuantity = mQuantity - 1;
                    //Must get the Uri of the product
                    Uri currentItemUri = ContentUris.withAppendedId(NewLeafContract.NewLeafEntry.CONTENT_URI, idColumnIndex);
                    ContentValues values = new ContentValues();
                    values.put(NewLeafContract.NewLeafEntry.COLUMN_QUANTITY, mQuantity);
                    //this is to update
                    context.getContentResolver().update(currentItemUri, values, null, null);
                }
            });
    }
}

