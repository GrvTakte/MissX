package com.ray.missreminder.subActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ray.missreminder.R;
import com.ray.missreminder.adapter.IgnoreAdapter;
import com.ray.missreminder.database.DbContract;
import com.ray.missreminder.database.DbHelper;
import com.ray.missreminder.database.IgnoreDbHelper;
import com.ray.missreminder.model.IgnoreModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gaurav on 3/7/2018.
 */

public class BlockNumber extends AppCompatActivity {

    List<IgnoreModel> list;
    ListView listView;

    Button clear_ignore_list;
    IgnoreAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block_number_activity);

        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.ignore_list_view);
        clear_ignore_list = (Button) findViewById(R.id.clear_ignore_list);

        adapter = new IgnoreAdapter(getApplicationContext(),R.layout.block_number_list_layout,list);
        listView.setAdapter(adapter);
        getDataFromDB();
    }

    @Override
    protected void onStart() {
        super.onStart();
        clear_ignore_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear all the data from blockedNumber table.
                IgnoreDbHelper helper = new IgnoreDbHelper(getApplicationContext());
                SQLiteDatabase database = helper.getWritableDatabase();
                helper.deleteAllData(database);
                helper.close();
                getDataFromDB();
                adapter.notifyDataSetChanged();
                listView.getFooterViewsCount();
            }
        });
    }

    private void getDataFromDB(){
        list.clear();
        IgnoreDbHelper helper = new IgnoreDbHelper(getApplicationContext());
        SQLiteDatabase database = helper.getWritableDatabase();

        Cursor cursor = helper.readBlockedNumbers(database);

        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){

                String number = cursor.getString(cursor.getColumnIndex(DbContract.BLOCKED_NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(DbContract.BLOCKED_NAME));

                list.add(new IgnoreModel(number,name));
            }

            cursor.close();
            helper.close();
            database.close();
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
