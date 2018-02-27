package com.gaurav.missreminder;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gaurav.missreminder.database.DbContract;
import com.gaurav.missreminder.database.DbHelper;

/**
 * Created by Gaurav on 2/23/2018.
 */

public class MyCustomDialog extends Activity {

    TextView number;
    String phone_no;
    Button remind,ignore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(false);
            super.onCreate(savedInstanceState);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            setContentView(R.layout.incoming_call_layout);
            initializeContent();

            /*WindowManager.LayoutParams params = getWindow().getAttributes();
            params.x = -100;
            params.height = 70;
            params.width = 1000;
            params.y = -50;

            this.getWindow().setAttributes(params);*/

            phone_no    =   getIntent().getExtras().getString("phone_no");
            number.setText(""+phone_no +" ?!");

            remind.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                     DbHelper helper = new DbHelper(getApplicationContext());
                    SQLiteDatabase database = helper.getWritableDatabase();
                    helper.saveNumber(phone_no, database);
                    helper.close();

                    Intent intent1 = new Intent(DbContract.REFRESH_LIST_UI);
                    getBaseContext().sendBroadcast(intent1);

                    MyCustomDialog.this.finish();
                }
            });

            ignore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyCustomDialog.this.finish();
                }
            });
        }
        catch (Exception e)
        {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }
    }

    private void initializeContent()
    {
        number   = (TextView) findViewById(R.id.incoming_number);
        remind   = (Button) findViewById(R.id.remind);
        ignore = (Button) findViewById(R.id.ignore);
    }

}
