package com.cotrav.recorder;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.RoomDatabase;


import com.cotrav.recorder.room.PhoneNumAPI;
import com.cotrav.recorder.room.data.CallRoomDatabase;
import com.cotrav.recorder.room.data.ConfigRetrofit;
import com.cotrav.recorder.room.data.OfflineDatabase;
import com.cotrav.recorder.room.data.upload.UploadResponse;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.MyViewHolder> {
    private FragmentActivity activity;
    private List<OfflineDatabase> offlineDatabases;
    private String bookingStatus;
    PhoneNumAPI phoneNumAPI;
    CallRoomDatabase roomDatabase;
    Context context;

    public CallHistoryAdapter(FragmentActivity activity, List<OfflineDatabase> offlineDatabases) {
        this.activity = activity;
        this.offlineDatabases = offlineDatabases;
        phoneNumAPI = ConfigRetrofit.configRetrofit(PhoneNumAPI.class);
    }

    @NonNull
    @Override
    public CallHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recording_list, parent, false);
        return new CallHistoryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CallHistoryAdapter.MyViewHolder viewHolder, int position) {

        viewHolder.phoneNo.setText(offlineDatabases.get(position).getPhoneNo());
        viewHolder.pickUpTime.setText(offlineDatabases.get(position).getCallPickUpTime());
        viewHolder.hangedTime.setText(offlineDatabases.get(position).getCallHangTime());
        viewHolder.date.setText(offlineDatabases.get(position).getDate());
        viewHolder.duration.setText(offlineDatabases.get(position).getCallDuration());
        String time1 = offlineDatabases.get(position).getCallPickUpTime();
        String time2 = offlineDatabases.get(position).getCallHangTime();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");
        Date date1 = null;
        Date date2 = null;
        try {
            if (time1 != null)
                date1 = format.parse(time1);
            if (time2 != null)
                date2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
/*
        if (date1 != null && date2 != null) {
            long difference = date2.getTime() - date1.getTime();


            long diffSeconds = difference / 1000 % 60;
            long diffMinutes = difference / (60 * 1000) % 60;
            long diffHours = difference / (60 * 60 * 1000) % 24;
            viewHolder.duration.setText("");
            if (diffHours != 0)
                viewHolder.duration.append("" + diffHours + "h ");
            if (diffMinutes != 0)
                viewHolder.duration.append("" + diffMinutes + "m ");
            if (diffSeconds != 0)
                viewHolder.duration.append("" + diffSeconds + "s ");


        }*/
        if (offlineDatabases.get(position).getUploaded() != null) {
            if (offlineDatabases.get(position).getUploaded().equals("yes")) {
                viewHolder.upload.setVisibility(View.GONE);
            } else viewHolder.upload.setVisibility(View.VISIBLE);
        } else viewHolder.upload.setVisibility(View.VISIBLE);


        if (offlineDatabases.get(position).getCallState() != null)
            if (offlineDatabases.get(position).getCallState().equals("out"))
                viewHolder.callStatus.setImageResource(R.drawable.ic_outgoing);
        if (offlineDatabases.get(position).getCallState() != null)
            if (offlineDatabases.get(position).getCallState().equals("in"))
                viewHolder.callStatus.setImageResource(R.drawable.ic_incoming);

    }

    @Override
    public int getItemCount() {
        return offlineDatabases.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView phoneNo, pickUpTime, hangedTime;
        private TextView duration, date;
        ImageView callStatus;
        ImageView upload;

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        public MyViewHolder(@NonNull View view) {
            super(view);
            phoneNo = view.findViewById(R.id.mobile_no);
            pickUpTime = view.findViewById(R.id.pickedup_timr);
            hangedTime = view.findViewById(R.id.hanged_time);
            duration = view.findViewById(R.id.duration);
            callStatus = view.findViewById(R.id.callstatus);
            date = view.findViewById(R.id.date);
            upload = view.findViewById(R.id.uploadbtn);
            upload.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int id = view.getId();
            final int position = getAdapterPosition();
            switch (id) {
                case R.id.uploadbtn:
                    if (offlineDatabases.get(position).getCallDuration() != null) {
                        String phoneNo = offlineDatabases.get(position).getPhoneNo();

                        if (phoneNo.startsWith("0")) {
                            String[] separated = phoneNo.split("0");
                            if (separated.length > 1)
                                phoneNo = separated[1];
                        } else if (phoneNo.startsWith("+91")) {
                            String[] separated = phoneNo.split("91");
                            if (separated.length > 1)
                                phoneNo = separated[1];
                        }

                        // phoneNumAPI = ConfigRetrofit.configRetrofit(PhoneNumAPI.class);
                        roomDatabase = CallRoomDatabase.getDatabase(context);
                        JsonObject json = new JsonObject();
                        json.addProperty("phone", phoneNo);
                        json.addProperty("call_duration", offlineDatabases.get(position).getCallDuration());
                        json.addProperty("call_date", offlineDatabases.get(position).getDate());
                        json.addProperty("call_details", "details");

                        phoneNumAPI.upload(json).enqueue(new Callback<UploadResponse>() {
                            @Override
                            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                                if (response.isSuccessful())
                                    if (response.body().getSuccess() != null) {
                                        roomDatabase.daoRecord().update(offlineDatabases.get(position).getDate());
                                        //  Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                                        Toasty.success(context, "Successful").show();

                                    }
                            }

                            @Override
                            public void onFailure(Call<UploadResponse> call, Throwable t) {
                                //  Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                Toasty.error(context, "Error").show();
                                ;
                            }
                        });
                        break;
                    } else {
                        // Toast.makeText(context, "Something is missing", Toast.LENGTH_SHORT).show();
                        Toasty.error(context, "Something is missing").show();
                    }
            }

        }
    }


}
