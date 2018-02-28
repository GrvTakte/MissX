package com.gaurav.missreminder.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaurav.missreminder.MissedCallModel;
import com.gaurav.missreminder.NotifyUser;
import com.gaurav.missreminder.R;
import com.gaurav.missreminder.adapter.ReminderAdapter;
import com.gaurav.missreminder.database.DbContract;
import com.gaurav.missreminder.database.DbHelper;
import com.gaurav.missreminder.subActivity.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gaurav on 2/21/2018.
 */

public class Reminders extends Fragment {

    ListView listView;
    List<MissedCallModel> list;
    ReminderAdapter adapter;

    Button clear;

    ImageView setting;

    private BroadcastReceiver broadcastReceiver;

    public BroadcastReceiver refreshReceiver;

    public BroadcastReceiver updateOutgoingReceiver;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View remindersView = inflater.inflate(R.layout.reminders_fragment,container, false);

        clear = (Button) remindersView.findViewById(R.id.clear_list);

        listView = (ListView) remindersView.findViewById(R.id.reminders_list_view);
        list = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(),R.layout.reminder_list_layout,list);
        listView.setAdapter(adapter);
        readFromDb();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromDb();
            }
        };

        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /*String number = intent.getStringExtra("number");
                DbHelper helper = new DbHelper(context);
                SQLiteDatabase database = helper.getWritableDatabase();
                helper.deleteRow(database,number);
                helper.close(); */
                readFromDb();
                adapter.notifyDataSetChanged();
                listView.getFooterViewsCount();
                Toast.makeText(context, "update receiver called", Toast.LENGTH_SHORT).show();
            }
        };

        updateOutgoingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromDb();
            }
        };

        setting = (ImageView) remindersView.findViewById(R.id.setting_view);
        return remindersView;
    }



    @Override
    public void onStart() {
        super.onStart();
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper helper = new DbHelper(getContext());
                SQLiteDatabase database = helper.getWritableDatabase();
                helper.clearDatabase(database);
                helper.close();
                readFromDb();
                adapter.notifyDataSetChanged();
                listView.getFooterViewsCount();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Setting.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadcastReceiver, new IntentFilter(DbContract.UPDATE_UI_FILTER));
        getContext().registerReceiver(refreshReceiver, new IntentFilter(DbContract.REFRESH_LIST_UI));
        getContext().registerReceiver(updateOutgoingReceiver, new IntentFilter(DbContract.REFRESH_OUTGOING_LIST));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastReceiver);
        getContext().unregisterReceiver(refreshReceiver);
        getContext().unregisterReceiver(updateOutgoingReceiver);
    }

    private void readFromDb(){
        list.clear();
        DbHelper dbHelper = new DbHelper(getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = dbHelper.readNumber(database);

        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                String number;
                int id;
                String name;

                number = cursor.getString(cursor.getColumnIndex(DbContract.INCOMING_NUMBER));
                id = cursor.getInt(cursor.getColumnIndex("id"));
                name = cursor.getString(cursor.getColumnIndex(DbContract.INCOMING_NAME));
               // Log.d("database List:",""+id+" "+number);
                list.add(new MissedCallModel(id,number,name));
            }
            cursor.close();
            dbHelper.close();
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
