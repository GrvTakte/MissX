package com.ray.missreminder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ray.missreminder.R;
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resourceId,null);

        TextView number = (TextView) view.findViewById(R.id.ignore_layout_number);
        Button remove_ignore = (Button) view.findViewById(R.id.remove_ignore);

        number.setText(list.get(position).getNumber());

        remove_ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        return view;
    }
}
