package com.cotrav.recorder.room.repo;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cotrav.recorder.room.data.CallRoomDatabase;
import com.cotrav.recorder.room.data.OfflineDatabase;

import java.util.Date;
import java.util.List;

public class FetchDataRepository {
    Application application;
    CallRoomDatabase callRoomDatabase;
    OfflineDatabase offlineDatabase;
    private LiveData<List<OfflineDatabase>> offlineDatabaseMutableLiveData;

    public FetchDataRepository(Application application) {
        this.application = application;
        offlineDatabaseMutableLiveData = new MutableLiveData<>();

        callRoomDatabase = CallRoomDatabase.getDatabase(application);
        offlineDatabaseMutableLiveData = callRoomDatabase.daoRecord().getAll();

    }

    public LiveData<List<OfflineDatabase>> getOfflineDatabaseMutableLiveData() {

        offlineDatabaseMutableLiveData = callRoomDatabase.daoRecord().getAll();

        return offlineDatabaseMutableLiveData;
    }

    public LiveData<List<OfflineDatabase>> getsetCallData() {

        getCallDetails(this.application);
      //  offlineDatabaseMutableLiveData = roomDatabase.daoRecord().getAll();

        return offlineDatabaseMutableLiveData;
    }

    public void setOfflineDatabaseMutableLiveData(LiveData<List<OfflineDatabase>> offlineDatabaseMutableLiveData) {
        this.offlineDatabaseMutableLiveData = offlineDatabaseMutableLiveData;
    }

    private void getCallDetails(Context context) {
        StringBuffer stringBuffer = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(this.application, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            stringBuffer.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            stringBuffer.append("\n----------------------------------");

            offlineDatabase = new OfflineDatabase();

            offlineDatabase.setPhoneNo(phNumber);
            offlineDatabase.setDate(""+callDayTime);
            offlineDatabase.setCallDuration(""+callDuration);

            if (dir.equals("OUTGOING"))
                offlineDatabase.setOutgoing("yes");
            if (dir.equals("INCOMING"))
                offlineDatabase.setIncoming("yes");

            callRoomDatabase.daoRecord().addRecording(offlineDatabase);

        }
        cursor.close();
    }
}
