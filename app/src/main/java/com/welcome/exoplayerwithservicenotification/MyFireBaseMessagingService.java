package com.welcome.exoplayerwithservicenotification;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    LocalBroadcastManager broadcastManager;
    public MyFireBaseMessagingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager=LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleMessage(remoteMessage);
    }

    private void handleMessage(RemoteMessage remoteMessage) {
        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent("MyData");
                String notificationBody= remoteMessage.getNotification().getBody();
                intent.putExtra("message",notificationBody);
                Log.d("NOTIFICATION",""+notificationBody);
                broadcastManager.sendBroadcast(intent);

            }
        });
    }
}