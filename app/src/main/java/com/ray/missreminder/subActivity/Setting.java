package com.ray.missreminder.subActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.ray.missreminder.R;

/**
 * Created by Gaurav on 2/27/2018.
 */

public class Setting extends AppCompatActivity {

    SeekBar seekBar;
    SharedPreferences preferences, reminderPref, reminderOutgoingPref, notesPref;
    SharedPreferences.Editor editor, reminderEditor, remindOutgoingEditor, notesEditor;
    Button done;

    Button ignoreListDisplay;
    TextView show_add_notes_text;

    boolean isChecked1=true;
    boolean isChecked2=true;


    Switch reminderSwitch, remindSwitchOutgoing, notesSwitch;
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

        //reminder outgoing prefernce
        reminderOutgoingPref = this.getSharedPreferences("reminderOutgoinfPref",0);
        remindOutgoingEditor = reminderOutgoingPref.edit();

        //notes shared preferences
        notesPref = this.getSharedPreferences("notes",0);
        notesEditor = notesPref.edit();

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        done = (Button) findViewById(R.id.setting_done);

        reminderSwitch = (Switch) findViewById(R.id.switch_remind_ignore);
        reminderSwitch.setChecked(true);

        notesSwitch = (Switch) findViewById(R.id.switch_notes);
        notesSwitch.setChecked(false);

        remindSwitchOutgoing = (Switch) findViewById(R.id.switch_remind_ignore_outgoing);

        show_add_notes_text = (TextView) findViewById(R.id.show_add_note_text);

        ignoreListDisplay = (Button) findViewById(R.id.ignore_list);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <=0){
                    seekBar.setProgress(0);
                    editor.clear();
                    editor.putInt("interval",5);
                    Log.d("missX","5");
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

        ignoreListDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, BlockNumber.class);
                startActivity(intent);
            }
        });

        checkSeekBarStatus();
    }

    private void checkSeekBarStatus(){
        //get notification interval
        int position = preferences.getInt("interval",0);

        //get boolean value for state like show reminder UI on callReceived, callDialed
        boolean showReminder = reminderPref.getBoolean("showReminder",true);
        boolean showOutgoingReminder = reminderOutgoingPref.getBoolean("showOutgoingReminder",true);
        boolean showNotesReminder = notesPref.getBoolean("showNotes",false);

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

        if (showOutgoingReminder){
            remindSwitchOutgoing.setChecked(true);
        }else {
            remindSwitchOutgoing.setChecked(false);
        }

        if (showNotesReminder){
            notesSwitch.setChecked(true);
        }else{
            notesSwitch.setChecked(false);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        final boolean showReminderOne = reminderPref.getBoolean("showReminder",true);
        final boolean showOutgoingReminderOne = reminderOutgoingPref.getBoolean("showOutgoingReminder",true);

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    reminderEditor.clear();
                    reminderEditor.putBoolean("showReminder",true);
                    reminderEditor.commit();

                   // notesSwitch.setVisibility(View.VISIBLE);
                   // show_add_notes_text.setVisibility(View.VISIBLE);
                    //notesSwitch.setChecked(true);
                }else {
                    reminderEditor.clear();
                    reminderEditor.putBoolean("showReminder",false);
                    reminderEditor.commit();

                    /* if (showOutgoingReminderOne==false){
                        //notesSwitch.setChecked(false);
                        notesSwitch.setVisibility(View.GONE);
                        show_add_notes_text.setVisibility(View.GONE);
                    } */
                }
            }
        });

        remindSwitchOutgoing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    remindOutgoingEditor.clear();
                    remindOutgoingEditor.putBoolean("showOutgoingReminder",true);
                    remindOutgoingEditor.commit();

                    //notesSwitch.setVisibility(View.VISIBLE);
                    //show_add_notes_text.setVisibility(View.VISIBLE);
                    //notesSwitch.setChecked(true);
                }else {
                    remindOutgoingEditor.clear();
                    remindOutgoingEditor.putBoolean("showOutgoingReminder",false);
                    remindOutgoingEditor.commit();

                    /*if (showReminderOne==false){
                       //notesSwitch.setChecked(false);
                        notesSwitch.setVisibility(View.GONE);
                        show_add_notes_text.setVisibility(View.GONE);
                    }*/
                }
            }
        });

        notesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    notesEditor.clear();
                    notesEditor.putBoolean("showNotes",true);
                    notesEditor.commit();
                }else {
                    notesEditor.clear();
                    notesEditor.putBoolean("showNotes",false);
                    notesEditor.commit();
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
