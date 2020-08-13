package com.cotrav.recorder.service;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.cotrav.recorder.other.App;
import com.cotrav.recorder.R;
import com.cotrav.recorder.room.data.CallRoomDatabase;
import com.cotrav.recorder.room.data.OfflineDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LifeTimeService extends Service {
    public LifeTimeService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        notification();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCallDetails();
                //Do something after 100ms
            }
        }, 200);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void notification() {
        //set a new Timer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(this, App.CHANNEL_1_ID)
                    .setContentTitle("Call Recording")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("Recording for lead")
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Call Recording")
                    .setContentText("Recording for lead")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);

        }

    }

    private void getCallDetails() {

        CallRoomDatabase roomDatabase;
        OfflineDatabase offlineDatabase;

        Uri contacts = CallLog.Calls.CONTENT_URI;
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Cursor managedCursor = getContentResolver().query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int incomingtype = managedCursor.getColumnIndex(String.valueOf(CallLog.Calls.INCOMING_TYPE));
            roomDatabase = CallRoomDatabase.getDatabase(this);
            offlineDatabase = new OfflineDatabase();
             { // added line

                while (managedCursor.moveToNext()) {
                    String callType = managedCursor.getString(type);

                    String phNumber = managedCursor.getString(number);
                  //  String callerName = getContactName(context, phNumber);
                    int callTypeCode = Integer.parseInt(callType);
                    switch (callTypeCode) {
                        case CallLog.Calls.OUTGOING_TYPE:

                            offlineDatabase.setOutgoing("yes");
                            offlineDatabase.setCallState("out");
                            break;

                        case CallLog.Calls.INCOMING_TYPE:

                            offlineDatabase.setIncoming("yes");
                            offlineDatabase.setCallState("in");
                            break;}

                    String callDate = managedCursor.getString(date);
                    String callDayTime = new Date(Long.valueOf(callDate)).toString();

                    String callDuration = managedCursor.getString(duration);

                  //  System.out.println("callDetails.setCallerName(" + callerName);
                    System.out.println("callDetails.setCallerNumber(" + phNumber);
                    System.out.println("callDetails.setCallDuration(" + callDuration);
                    System.out.println("callDetails.setCallType(" + callType);
                    System.out.println("callDetails.setCallTimeStamp(" + callDayTime);

                    offlineDatabase.setPhoneNo(phNumber);
                    String out = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format( new Date(Long.valueOf(callDate)));
                    offlineDatabase.setDate(out);
                    offlineDatabase.setCallDuration(callDuration+"s");

                    roomDatabase.daoRecord().insert(offlineDatabase);
                }
            }
            managedCursor.close();

        } catch (SecurityException e) {
            Log.e("Security Exception", "User denied call log permission");

        }
        stopSelf();
    }

}
