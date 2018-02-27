package com.gaurav.missreminder;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.gaurav.missreminder.adapter.FragmentPagerAdapter;
import com.gaurav.missreminder.fragment.MissedCall;
import com.gaurav.missreminder.fragment.Notes;
import com.gaurav.missreminder.fragment.Reminders;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager pager;

    MenuItem menuItem;
    Intent mServiceIntent;
    private NotifyUser notifyUser;
    Context context;


    private MainActivity mainActivity;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;
    private static final String[] PERMISSION_REQUIURED = {Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS};

    public Context getContext(){
        return context;
    }


    //fragments
    MissedCall missedCall;
    Reminders reminders;
    Notes notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;

        checkPermissions();

        setContentView(R.layout.activity_main);
        copyOnCreate();
    }


    private boolean checkPermission(String permissions[]) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void checkPermissions() {
        boolean permissionsGranted = checkPermission(PERMISSION_REQUIURED);
        if (permissionsGranted) {
            Toast.makeText(this, "You've granted all required permissions!", Toast.LENGTH_SHORT).show();
        } else {
            boolean showRationale = true;
            for (String permission: PERMISSION_REQUIURED) {
                showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                if (!showRationale) {
                    break;
                }
            }

            String dialogMsg = showRationale ? "We need some permissions to run this APP!" : "You've declined the required permissions, please grant them from your phone settings";

            new AlertDialog.Builder(this)
                    .setTitle("Permissions Required")
                    .setMessage(dialogMsg)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(mainActivity, PERMISSION_REQUIURED, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    }).create().show();
        }
    }

    private void copyOnCreate(){
        //Start never ending service
        context = this;
        notifyUser = new NotifyUser(getContext());

        mServiceIntent = new Intent(getContext(),notifyUser.getClass());
        if (!isServiceRunning(notifyUser.getClass())){
            startService(mServiceIntent);
        }

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        pager = (ViewPager) findViewById(R.id.viewPager);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_missed_calls:
                        pager.setCurrentItem(0);
                        break;
                    case R.id.nav_reminders:
                        pager.setCurrentItem(1);
                        break;
                    case R.id.nav_notes:
                        pager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem!=null){
                    menuItem.setChecked(false);
                }else {
                    bottomNavigationView.getMenu().getItem(1).setChecked(false);
                }

                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                menuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager();
    }

    private boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        super.onDestroy();
    }

    private void setupViewPager(){
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager());
        missedCall = new MissedCall();
        reminders = new Reminders();
        notes = new Notes();
        adapter.addFragment(missedCall);
        adapter.addFragment(reminders);
        adapter.addFragment(notes);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("MainActivity", "requestCode: " + requestCode);
        Log.d("MainActivity", "Permissions:" + Arrays.toString(permissions));
        Log.d("MainActivity", "grantResults: " + Arrays.toString(grantResults));

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            boolean hasGrantedPermissions = true;
            for (int i=0; i<grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    hasGrantedPermissions = false;
                    break;
                }
            }

            if (!hasGrantedPermissions) {
                finish();
            }

        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
