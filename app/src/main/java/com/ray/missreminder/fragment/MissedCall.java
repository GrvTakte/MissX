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

import com.ray.missreminder.R;

/**
 * Created by Gaurav on 2/21/2018.
 */

public class MissedCall extends Fragment {

    TextView feedback;
    ImageView whatsapp_share;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View misssedCallView = inflater.inflate(R.layout.missed_call_fragment, container, false);

        feedback = (TextView) misssedCallView.findViewById(R.id.feedback_text);
        feedback.setPaintFlags(feedback.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        whatsapp_share = (ImageView) misssedCallView.findViewById(R.id.share_whatsapp);

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

        whatsapp_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = "*MissX - Call reminder app* \n\n This reminds you of your missed calls at a regular interval. "+
                        "The interval can be set according to your needs. Optionally one can choose for displaying an onscreen option"+
                        " to remind for a received or called number. \n\n INSTALL NOW: \n https://play.google.com/store/apps/details?id=com.ray.missreminder " ;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_TEXT,shareText);
                startActivity(intent);
            }
        });

        return misssedCallView;
    }
}
