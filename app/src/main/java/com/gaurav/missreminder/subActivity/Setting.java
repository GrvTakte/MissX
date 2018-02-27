package com.gaurav.missreminder.subActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.gaurav.missreminder.R;

/**
 * Created by Gaurav on 2/27/2018.
 */

public class Setting extends AppCompatActivity {

    SeekBar seekBar;
    SharedPreferences preferences, reminderPref;
    SharedPreferences.Editor editor, reminderEditor;
    Button done;

    Switch reminderSwitch, notesSwitch;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        //notification preference
        preferences = this.getSharedPreferences("notificationSetting",0);
        editor = preferences.edit();

        //reminder preference
        reminderPref = this.getSharedPreferences("reminderPreference",0);
        reminderEditor = reminderPref.edit();

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        done = (Button) findViewById(R.id.setting_done);

        reminderSwitch = (Switch) findViewById(R.id.switch_remind_ignore);
        reminderSwitch.setChecked(true);
        //notesSwitch = (Switch) findViewById(R.id.switch_notes);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <=0){
                    seekBar.setProgress(0);
                    editor.clear();
                    editor.putInt("interval",0);
                    Log.d("missX","0");
                    editor.commit();
                }
                else if (progress >0 && progress < 33){
                    seekBar.setProgress(33);
                    editor.clear();
                    editor.putInt("interval",10);
                    editor.commit();
                }else if (progress > 33 && progress < 66){
                    seekBar.setProgress(66);
                    editor.clear();
                    editor.putInt("interval",20);
                    editor.commit();
                }else if (progress > 66){
                    seekBar.setProgress(100);
                    editor.clear();
                    editor.putInt("interval",30);
                    editor.commit();
                }
                Log.d("missX","interval"+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        checkSeekBarStatus();
    }

    private void checkSeekBarStatus(){
        int position = preferences.getInt("interval",0);
        boolean showReminder = reminderPref.getBoolean("showReminder",true);

        Log.d("missX ","pref "+position);

        if (position <= 0 ){
            seekBar.setProgress(0);
        }
        else if (position<=33){
            seekBar.setProgress(33);
        }else if(position >33 && position <=66){
            seekBar.setProgress(66);
        }else if (position >66){
            seekBar.setProgress(100);
        }

        if (showReminder){
            reminderSwitch.setChecked(true);
        }else{
            reminderSwitch.setChecked(false);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    reminderEditor.clear();
                    reminderEditor.putBoolean("showReminder",true);
                    reminderEditor.commit();
                }else {
                    reminderEditor.clear();
                    reminderEditor.putBoolean("showReminder",false);
                    reminderEditor.commit();
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.this.finish();
            }
        });
    }
}
