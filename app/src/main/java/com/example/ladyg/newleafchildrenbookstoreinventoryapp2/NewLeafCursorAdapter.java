package com.example.ladyg.newleafchildrenbookstoreinventoryapp2;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_QUANTITY);
        int suppliernameColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_NAME);
        int supplierphonenumberColumnIndex = cursor.getColumnIndex(NewLeafContract.NewLeafEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);
        String bookSuppliername = cursor.getString(suppliernameColumnIndex);
        String bookSupplierphonenumber= cursor.getString(supplierphonenumberColumnIndex);

        // If the supplier phone number is empty string or null, then use some default text
        // that says "Unknown phone number of the supplier", so the TextView isn't blank.
        if (TextUtils.isEmpty(bookSupplierphonenumber)) {
            bookSupplierphonenumber = context.getString(R.string.unknown_phone_number);
        }

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(bookName);
        summaryTextView.setText(bookPrice);
        summaryTextView.setText(bookQuantity);
        summaryTextView.setText(bookSuppliername);
        summaryTextView.setText(bookSupplierphonenumber);
    }
}

