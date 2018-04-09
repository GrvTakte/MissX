package com.ray.missreminder.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ray.missreminder.R;
import com.ray.missreminder.adapter.NotesAdapter;
import com.ray.missreminder.database.DbContract;
import com.ray.missreminder.database.DbNotesHelper;
import com.ray.missreminder.model.NotesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gaurav on 2/21/2018.
 */

public class Notes extends Fragment {

    List<NotesModel> list;
    ListView notesListView;
    NotesAdapter adapter;

    BroadcastReceiver refreshReceiver;
    BroadcastReceiver removeRefreshReceiver;

    Button clear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View notesView = inflater.inflate(R.layout.notes_fragment, container, false);
        try {
            notesListView = (ListView) notesView.findViewById(R.id.notes_list_view);
            list = new ArrayList<>();
            clear = (Button) notesView.findViewById(R.id.clear_notes);

            adapter = new NotesAdapter(getContext(), R.layout.notes_list_layout, list);
            notesListView.setAdapter(adapter);
            getNotesData();

            refreshReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    getNotesData();
                }
            };

            removeRefreshReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    getNotesData();
                    adapter.notifyDataSetChanged();
                    notesListView.getFooterViewsCount();
                }
            };
        }catch (Exception e){
            e.printStackTrace();
        }
        return notesView;
    }

    @Override
    public void onStart() {
        super.onStart();
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // use observable desig patter here so the UI and data sync dynamically.
                DbNotesHelper helper = new DbNotesHelper(getContext());
                SQLiteDatabase database = helper.getWritableDatabase();
                helper.clearDatabase(database);
                helper.close();
                getNotesData();
                adapter.notifyDataSetChanged();
                notesListView.getFooterViewsCount();
            }
        });
    }

    private void getNotesData(){
        list.clear();
        DbNotesHelper helper = new DbNotesHelper(getContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = helper.readNotes(database);

        if (cursor.getCount() >0){
            while (cursor.moveToNext()){
                String number;
                int id;
                String name;
                String notes;

                id = cursor.getInt(cursor.getColumnIndex("id"));
                number = cursor.getString(cursor.getColumnIndex(DbContract.NOTES_NUMBER));
                notes = cursor.getString(cursor.getColumnIndex(DbContract.NOTES_TEXT));
                name = cursor.getString(cursor.getColumnIndex(DbContract.NOTES_NAME));

                list.add(new NotesModel(id,number,notes,name));
            }
            cursor.close();
            helper.close();
            notesListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(refreshReceiver);
        getContext().unregisterReceiver(removeRefreshReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(refreshReceiver,new IntentFilter(DbContract.REFRESH_NOTES_UI));
        getContext().registerReceiver(removeRefreshReceiver,new IntentFilter(DbContract.REMOVE_REFRESH_NOTES_UI));
    }
}