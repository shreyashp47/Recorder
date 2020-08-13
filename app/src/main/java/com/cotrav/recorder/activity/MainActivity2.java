package com.cotrav.recorder.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.TextView;

import com.cotrav.recorder.CallHistoryAdapter;
import com.cotrav.recorder.R;
import com.cotrav.recorder.room.data.CallRoomDatabase;
import com.cotrav.recorder.room.repo.FetchDataViewModel;
import com.cotrav.recorder.room.data.OfflineDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    CallRoomDatabase roomDatabase;
    TextView norecord;
    List<OfflineDatabase> offlineDatabase;
    CallHistoryAdapter callHistoryAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    FetchDataViewModel fetchDataViewModel;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        roomDatabase = CallRoomDatabase.getDatabase(this);
        fetchDataViewModel = ViewModelProviders.of(this).get(FetchDataViewModel.class);

        norecord = findViewById(R.id.noRecord);
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        offlineDatabase = new ArrayList<>();

        callHistoryAdapter = new CallHistoryAdapter(this, offlineDatabase);
        recyclerView = (RecyclerView) findViewById(R.id.onoff);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //Log.d("Archived", "Running archived fragment");
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(callHistoryAdapter);
        fetchDataViewModel.setData();

        fetchDataViewModel.getOfflineDataList().observe(this, new Observer<List<OfflineDatabase>>() {
            @Override
            public void onChanged(List<OfflineDatabase> offlineDatabases) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (offlineDatabases != null) {
                    offlineDatabase.clear();
                    offlineDatabase.addAll(offlineDatabases);
                }
                if (offlineDatabases.size() == 0) {
                    norecord.setVisibility(View.VISIBLE);
                } else norecord.setVisibility(View.GONE);

                callHistoryAdapter.notifyDataSetChanged();
            }
        });


    }


    private void getCallDetails() {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Log :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
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
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        } //managedCursor.close();
        textView.setText(sb);
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        fetchDataViewModel.getOfflineDataList();
    }
}