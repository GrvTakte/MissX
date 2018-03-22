package com.ray.missreminder.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ray.missreminder.R;

/**
 * Created by Gaurav on 2/21/2018.
 */

public class MissedCall extends Fragment {

    TextView feedback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View missedCallView = inflater.inflate(R.layout.missed_call_fragment, container, false);

        feedback = (TextView) missedCallView.findViewById(R.id.feedback_text);
        feedback.setPaintFlags(feedback.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://play.google.com/store/apps/details?id=com.ray.missreminder";
                Intent rateIntent = new Intent();
                rateIntent.setAction(Intent.ACTION_VIEW);
                rateIntent.setData(Uri.parse(url));
                startActivity(rateIntent);
            }
        });

        return missedCallView;
    }
}