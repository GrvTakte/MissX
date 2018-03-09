package com.ray.missreminder.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ray.missreminder.R;
import com.ray.missreminder.database.DbContract;
import com.ray.missreminder.database.IgnoreDbHelper;
import com.ray.missreminder.model.IgnoreModel;

import java.util.List;

/**
 * Created by Gaurav on 3/7/2018.
 */

public class IgnoreAdapter extends ArrayAdapter<IgnoreModel>{

    Context context;
    int resourceId;
    List<IgnoreModel> list;

    public IgnoreAdapter(Context context, int resourceId, List<IgnoreModel> list){
        super(context,resourceId,list);
        this.context = context;
        this.resourceId = resourceId;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resourceId,null);

        TextView number = (TextView) view.findViewById(R.id.ignore_layout_number);
        TextView name = (TextView) view.findViewById(R.id.ignore_layout_name);
        Button remove_ignore = (Button) view.findViewById(R.id.remove_ignore);

        number.setText("+"+list.get(position).getNumber());
        name.setText(list.get(position).getName());

        remove_ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IgnoreDbHelper helper = new IgnoreDbHelper(context);
                SQLiteDatabase database = helper.getWritableDatabase();
                helper.deleteRow(database,list.get(position).getNumber());

                Cursor cursor = helper.readBlockedNumbers(database);

                if (cursor.getCount()>0){
                    while (cursor.moveToNext()){
                        String number = cursor.getString(cursor.getColumnIndex(DbContract.BLOCKED_NUMBER));
                        String name = cursor.getString(cursor.getColumnIndex(DbContract.BLOCKED_NAME));
                    }
                }
                cursor.close();
                helper.close();
                Intent intent = new Intent(DbContract.REFRESH_IGNORE_LIST);
                context.sendBroadcast(intent);
            }
        });

        return view;
    }
}
