package com.example.riyasharma.sms;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by RiyaSharma on 05-02-2017.
 */
public class TodoCursorAdapter extends CursorAdapter {
    public TodoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor,0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView address = (TextView) view.findViewById(R.id.txt_msgTO);
        TextView message = (TextView) view.findViewById(R.id.txt_messageContent);
        TextView threadId=(TextView)view.findViewById(R.id.txt_threadId);
        TextView Date=(TextView)view.findViewById(R.id.txt_date);
        // Extract properties from cursor
        String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
        String  body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
        String thread=cursor.getString(cursor.getColumnIndex("thread_id"));
        String date=millisToDate(cursor.getColumnIndex("date"));
        // Populate fields with extracted properties
        address.setText(sender);
        message.setText(body);
        threadId.setText(thread);
        Date.setText(date);
    }
    public static String millisToDate(long currentTime) {
        String finalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        Date date = calendar.getTime();
        finalDate = date.toString();
        return finalDate;
    }
}