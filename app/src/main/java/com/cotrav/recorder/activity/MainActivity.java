package com.cotrav.recorder.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cotrav.recorder.CallHistoryAdapter;
import com.cotrav.recorder.R;
import com.cotrav.recorder.room.data.CallRoomDatabase;
import com.cotrav.recorder.room.repo.AllPhoneNoViewModel;
import com.cotrav.recorder.room.repo.FetchDataViewModel;
import com.cotrav.recorder.room.data.OfflineDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    CallRoomDatabase roomDatabase;
    TextView norecord;
    List<OfflineDatabase> offlineDatabase;
    CallHistoryAdapter callHistoryAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FetchDataViewModel fetchDataViewModel;
    AllPhoneNoViewModel allPhoneNoViewModel;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    AlertDialog.Builder dialog;
    Button checkin, checkout;
    TextView intime, outtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent serviceIntent = new Intent(getApplicationContext(), LifeTimeService.class);
//        if (isMyServiceRunning(getApplicationContext(), LifeTimeService.class)) {
//            stopService(serviceIntent);
//        }
        // startService(serviceIntent);

        dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Something Went Wrong");
        dialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                allPhoneNoViewModel.getPhoneNoList();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Phone Numbers");
        progressDialog.setCancelable(false);
        progressDialog.show();
        roomDatabase = CallRoomDatabase.getDatabase(this);
        fetchDataViewModel = ViewModelProviders.of(this).get(FetchDataViewModel.class);
        allPhoneNoViewModel = ViewModelProviders.of(this).get(AllPhoneNoViewModel.class);

        norecord = findViewById(R.id.noRecord);
        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar = findViewById(R.id.progressbar);
        intime = findViewById(R.id.intime);
        outtime = findViewById(R.id.outtime);
        checkin = findViewById(R.id.checkin);
        checkout = findViewById(R.id.checkout);
        offlineDatabase = new ArrayList<>();

        callHistoryAdapter = new CallHistoryAdapter(this, offlineDatabase);
        recyclerView = (RecyclerView) findViewById(R.id.onoff);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        //Log.d("Archived", "Running archived fragment");
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(callHistoryAdapter);


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

        progressBar.setVisibility(View.VISIBLE);
        allPhoneNoViewModel.getPhoneNoList().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("OK")) {
                    progressBar.setVisibility(View.GONE);
                    progressDialog.dismiss();
                }
                if (s.equals("ERROR")) {
                    progressBar.setVisibility(View.GONE);
                    progressDialog.dismiss();
                    dialog.show();
                }
            }
        });
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{READ_CALL_LOG, READ_PHONE_STATE}, 1);

        }

        checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String out = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date());
                intime.setText(out);
            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String out = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date());
                outtime.setText(out);
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALL_LOG);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {

            swipeRefreshLayout.setRefreshing(false);
            fetchDataViewModel.getOfflineDataList();

        }
        // fetchDataViewModel.getOfflineDataList();
    }

    private static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean smsAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && smsAccepted)
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    else {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != 1)
                            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS) != 1)
                            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA},
                                        1);
                            }

                            return;
                        }

                    }
                }
                break;

        }

    }

}