package com.example.jacoco_epic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        startForeground(10010, new Notification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return flags;
        }
        if (intent.getBooleanExtra("test", false)) {
            return flags;
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
