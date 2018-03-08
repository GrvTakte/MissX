package com.ray.missreminder.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ray.missreminder.MissedCallModel;
import com.ray.missreminder.R;
import com.ray.missreminder.database.DbContract;
import com.ray.missreminder.database.DbHelper;
import com.ray.missreminder.database.IgnoreDbHelper;

import java.util.List;

/**
 * Created by Gaurav on 2/22/2018.
 */

public class ReminderAdapter extends ArrayAdapter<MissedCallModel>{

 private Context context;
 private List<MissedCallModel> list;
 private int resourceId;

 public ReminderAdapter(Context context, int resourceId, List<MissedCallModel> list){
     super(context, resourceId, list);
     this.context = context;
     this.resourceId = resourceId;
     this.list = list;
 }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resourceId,null);

        TextView number = (TextView) view.findViewById(R.id.reminder_layout_number);
        TextView name = (TextView) view.findViewById(R.id.reminder_layout_name);
        Button removeButton = (Button) view.findViewById(R.id.remove_reminder);
        Button ignoreButton = (Button) view.findViewById(R.id.ignore_reminder);
        number.setText(list.get(position).getNumber());
        name.setText(list.get(position).getName());


        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper helper = new DbHelper(context);
                SQLiteDatabase database = helper.getWritableDatabase();
                helper.deleteRow(database,list.get(position).getNumber());

                Cursor cursor = helper.readNumber(database);

                if (cursor.getCount()>0){
                    while (cursor.moveToNext()) {
                        String number;
                        int id;

                        number = cursor.getString(cursor.getColumnIndex(DbContract.INCOMING_NUMBER));
                        id = cursor.getInt(cursor.getColumnIndex("id"));
                        Log.d("Id: ", "" + id);
                        Log.d("number", number);
                    }
                }
                cursor.close();
                helper.close();
                Intent intent1 = new Intent(DbContract.REFRESH_LIST_UI);
                context.sendBroadcast(intent1);
            }
        });


        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    // remove item from reminder table and add it to the ignore table.
                    IgnoreDbHelper helper = new IgnoreDbHelper(context);
                    SQLiteDatabase database = helper.getWritableDatabase();
                    helper.saveNumber(list.get(position).getNumber(), list.get(position).getName() ,database);
                    Log.d("Ignore number",list.get(position).getNumber());
                    helper.close();

                    DbHelper helper1 = new DbHelper(context);
                    SQLiteDatabase db = helper1.getWritableDatabase();
                    helper1.deleteRow(db, list.get(position).getNumber());

                    Cursor cursor1 = helper1.readNumber(db);

                    if (cursor1.getCount() > 0) {
                        while (cursor1.moveToNext()) {
                            String number;
                            int id;

                            number = cursor1.getString(cursor1.getColumnIndex(DbContract.INCOMING_NUMBER));
                            id = cursor1.getInt(cursor1.getColumnIndex("id"));
                            Log.d("Id: ", "" + id);
                            Log.d("number", number);
                        }
                    }
                    cursor1.close();
                    helper1.close();
                    Intent intent1 = new Intent(DbContract.REFRESH_LIST_UI);
                    context.sendBroadcast(intent1);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
