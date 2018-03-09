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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ray.missreminder.R;
import com.ray.missreminder.database.DbContract;
import com.ray.missreminder.database.DbNotesHelper;
import com.ray.missreminder.model.NotesModel;

import java.util.List;

/**
 * Created by Gaurav on 3/1/2018.
 */

public class NotesAdapter extends ArrayAdapter<NotesModel>{

    Context context;
    List<NotesModel> list;
    int resourceId;

    public NotesAdapter(Context context, int resourceId, List<NotesModel> list){
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

        TextView number, notes, name;
        ImageButton remove;

        number = (TextView) view.findViewById(R.id.notes_layout_number);
        notes = (TextView) view.findViewById(R.id.notes_layout_save_text);
        remove = (ImageButton) view.findViewById(R.id.notes_layout_remove);
        name = (TextView) view.findViewById(R.id.notes_layout_name);

        number.setText(list.get(position).getNumber());
        notes.setText(list.get(position).getNotesText());
        name.setText(list.get(position).getName());

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbNotesHelper helper = new DbNotesHelper(context);
                SQLiteDatabase database = helper.getWritableDatabase();
                helper.deleteRow(database,list.get(position).getNumber());

                Cursor cursor = helper.readNotes(database);

                if (cursor.getCount()>0){
                    while (cursor.moveToNext()) {
                        String number;
                        int id;

                        number = cursor.getString(cursor.getColumnIndex(DbContract.NOTES_NUMBER));
                        id = cursor.getInt(cursor.getColumnIndex("id"));
                        Log.d("Id: ", "" + id);
                        Log.d("number", number);
                    }
                }
                helper.close();
                Intent intent1 = new Intent(DbContract.REMOVE_REFRESH_NOTES_UI);
                context.sendBroadcast(intent1);
            }
        });

        return view;
    }
}
