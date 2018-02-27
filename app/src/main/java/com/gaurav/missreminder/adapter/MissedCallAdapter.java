package com.gaurav.missreminder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gaurav.missreminder.MissedCallModel;
import com.gaurav.missreminder.R;
import com.gaurav.missreminder.fragment.MissedCall;

import java.util.List;

/**
 * Created by Gaurav on 2/22/2018.
 */

public class MissedCallAdapter extends ArrayAdapter<String> {

    List<String> lst;
    Context context;
    int resourceId;

    public MissedCallAdapter(Context context, int resourceId, List<String> lst){
        super(context,resourceId,lst);
        this.context = context;
        this.resourceId = resourceId;
        this.lst = lst;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resourceId,null);

        TextView missed_call = (TextView) view.findViewById(R.id.missed_call_numbers);
        missed_call.setText(lst.get(position).toString());
        return view;
    }

    @Override
    public int getCount() {
        return lst.size();
    }
}
