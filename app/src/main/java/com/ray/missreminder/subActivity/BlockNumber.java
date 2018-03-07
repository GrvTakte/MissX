package com.ray.missreminder.subActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ray.missreminder.R;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block_number_activity);

        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.ignore_list_view);
        clear_ignore_list = (Button) findViewById(R.id.clear_ignore_list);

        clear_ignore_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear all the data from blockedNumber table
            }
        });
    }
}
