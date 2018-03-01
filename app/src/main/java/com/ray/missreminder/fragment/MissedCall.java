package com.ray.missreminder.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ray.missreminder.R;

import java.util.List;

/**
 * Created by Gaurav on 2/21/2018.
 */

public class MissedCall extends Fragment {

    private ListView listView;
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    List<String> lst;

    TextView textView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View misssedCallView = inflater.inflate(R.layout.missed_call_fragment, container, false);

       /* listView = (ListView) misssedCallView.findViewById(R.id.missed_call_list);
        lst = new ArrayList<String>();
        //textView = (TextView) misssedCallView.findViewById(R.id.textCall);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
        }else {

            StringBuffer sb = new StringBuffer();
            Cursor managedCursor = getActivity().managedQuery(CallLog.Calls.CONTENT_URI,null, null,null, null);

            int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
            int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
            int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);

            sb.append("Call Details");

            while(managedCursor.moveToNext()){

                String callType = managedCursor.getString( type );
                String dir = null;
                int dircode = Integer.parseInt( callType );

                if (dircode == CallLog.Calls.MISSED_TYPE){
                    Date c = Calendar.getInstance().getTime();
                    String currentDate = DateFormat.getDateInstance().format(c);

                    String callDate = managedCursor.getString( date );
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String dateString = DateFormat.getDateInstance().format(callDayTime);
                    if (dateString.matches(currentDate)) {
                        String phNumber = managedCursor.getString(number);
                        String callDuration = managedCursor.getString(duration);
                        sb.append( "\nPhone Number:--- "+phNumber +" \n Call Date:--- "+dateString+" \nCall duration in sec :--- "+callDuration );
                        sb.append("\n----------------------------------");
                        lst.add(""+sb);
                    }
                }
            }
            managedCursor.close();
            //textView.setText(sb);
            MissedCallAdapter adapter = new MissedCallAdapter(getContext(),R.layout.missed_call_list_layout,lst);
            listView.setAdapter(adapter);
        } */
        return misssedCallView;
    }


    private void checkPermission(){
        int permissionCheck = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setTitle("Storage Permission");
                builder.setMessage("This application require storage permission");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_MULTIPLE_REQUEST);
                    }
                });
                builder.show();
            } else {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setTitle("Storage Permission");
                builder.setMessage("This application require storage permission");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_MULTIPLE_REQUEST);
                    }
                });
                builder.show();
            }
        } else {
            // got permission use it
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length >= 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, do your work....
                    Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    // Disable the functionality that depends on this permission.
                }
                return;
            }
            // other 'case' statements for other permissions
        }
    }


}
