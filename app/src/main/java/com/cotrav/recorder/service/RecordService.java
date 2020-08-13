package com.cotrav.recorder.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.documentfile.provider.DocumentFile;

import com.cotrav.recorder.other.App;
import com.cotrav.recorder.activity.MainActivity;
import com.cotrav.recorder.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordService extends Service {

    private MediaRecorder recorder;
    private boolean recodSrart;
    private DocumentFile file;

    private File audiofile;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        startRecording();
        //startService();
        notification();
        Toast.makeText(this, "OnStart CMD", Toast.LENGTH_SHORT).show();

        //TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       /* String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? createNotificationChannel(notificationManager) : "";*/
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, App.CHANNEL_1_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Call analytics")
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(1, notification);
    }

    public void notification() {
        //set a new Timer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Notification.Builder builder = new Notification.Builder(this, App.CHANNEL_1_ID)

                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(this.getString(R.string.notification_title))
                    .setTicker(this.getString(R.string.notification_ticker))
                    .setContentText(this.getString(R.string.notification_text))
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);

        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(this.getString(R.string.notification_title))
                    .setTicker(this.getString(R.string.notification_ticker))
                    .setContentText(this.getString(R.string.notification_text))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            Notification notification = builder.build();

            startForeground(1, notification);
        }
    }

    private void startService() {

        Log.d(Constants.TAG, "RecordService startService");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getBaseContext(), 0, intent, 0);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)

                .setContentTitle(this.getString(R.string.notification_title))
                .setTicker(this.getString(R.string.notification_ticker))
                .setContentText(this.getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        notification.flags = Notification.FLAG_NO_CLEAR;
        //   notificationManagerCompat.notify(1, notification);
        startForeground(0, notification);


    }

    private void stopService() {
        Log.d(Constants.TAG, "RecordService stopService");
        stopForeground(true);
        //  onForeground = false;
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
        if (recodSrart) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recodSrart = false;
            Log.d("Constants.TAG", "RecordService: Recorder Stoped!");
        }
        Toast.makeText(this, "onDestroy CMD", Toast.LENGTH_SHORT).show();
    }


    private void startRecording() {
        Log.d("TAG", "RecordService startRecording");
        recorder = new MediaRecorder();

        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            String fileName = getFilesDir().getAbsolutePath();
            String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());

            fileName += "/" + out + "record.3gp";
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, AUDIO_RECORDER_FOLDER);
            boolean success = true;
            if (!file.exists()) {
                success = file.mkdirs();
            }

            recorder.setOutputFile(fileName);
            //recorder.setOutputFile(fd.getFileDescriptor());

            recorder.prepare();

            // Sometimes the recorder takes a while to start up
            Thread.sleep(1000);

            recorder.start();
            recodSrart = true;

            Log.d("Constants.TAG", "RecordService: Recorder started!");
            Toast toast = Toast.makeText(this,
                    "receiver_start_call",
                    Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            Log.e("Constants.TAG", "Failed to set up recorder.", e);
            // terminateAndEraseFile();
            Toast toast = Toast.makeText(this, "record_impossible",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }


    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";


}
