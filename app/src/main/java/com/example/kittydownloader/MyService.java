package com.example.kittydownloader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyService extends Service {
    public static final String CHANNEL_NAME = "mykittydownloaderchannel";
    private static final int NOTIFICATION_ID = 185697584;
    public static final String CHANNEL_ID = "55g8t8dh88r9dj8556v6fhdg";

    FileOutputStream outStream;
    File file;


    @Override
    public int onStartCommand(Intent intent, int flags, int id) {
        // get lasturl form activity intent

        if(intent!=null){
        if (intent.getAction()=="downloading") {
            String url = intent.getStringExtra("lasturl");
            download(url);
        }}
        return START_STICKY; // stay running
    }


    public void download(String url) {


        // download the image as a bitmap file from url, this code could be put inside a thread if there where too many pictures
        // to download at the same time
        Ion.with(this).load(url).withBitmap().asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception exception, Bitmap bitmap) {
                        // do something with your bitmap
                        //  creating the file in the Downloads directory
                        Log.i("directory", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "kitty" + String.valueOf(System.currentTimeMillis()
                        ) + ".png");
                        Log.i("filename", file.getName());

                        try {
                            outStream = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        //put the Image into the file :

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        Toast.makeText(MyService.this, "download started", Toast.LENGTH_LONG).show();

                        try {
                            outStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            outStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        // Sending a broadcast to the operating system that it's done
                        //Toast.makeText(MyService.this, "download finished", Toast.LENGTH_LONG).show();
                        Intent done = new Intent();
                        done.setAction("download complete");
                        done.putExtra("message", file.getName() + "was downloaded");
                        sendBroadcast(done);


                        //make a notification about it
                        makeNotification(url);

                    }
                });
    }

    public void makeNotification(String url) {


        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            makeNotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("file Downloaded")
                    .setContentText("file url :" + url)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.icons8)
                    ;

            // when notification clicked it jumps you into the activity :

            Intent intent2 = new Intent(MyService.this, MainActivity.class);
            intent2.putExtra("filename", file.getName());
            intent2.setAction("download complete message");

            PendingIntent pending = PendingIntent.getActivity(this, 0, intent2, 0);
            builder.setContentIntent(pending);

            // build notification

            Notification notification = builder.build();
            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, notification);

        } else {
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle("file Downloaded")
                    .setContentText("file url :" + url)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.icons8);

            // when notification clicked, it jumps you into the activity :

            Intent intent3 = new Intent(this, MainActivity.class);
            intent3.putExtra("filename", file.getName());
            intent3.setAction("download complete message");

            PendingIntent pending = PendingIntent.getActivity(this, 0, intent3, 0);
            builder.setContentIntent(pending);

            // build notification

            Notification notification = builder.build();
            NotificationManager manager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, notification);

        }


    }

    // Helper method to create a notification channel for
    // Android 8.0+
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void makeNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(
                channel);
    }


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");


    }
}