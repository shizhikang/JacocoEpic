package com.example.jacoco_epic;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

public class MyService2 extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(10010, new Notification());
        testDialog();
    }
    public void testDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("test")
                .setMessage("just test")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("shizhikang - testDialog");
                    }
                });
        AlertDialog alertDialog = builder.create();
        int type;
        if (Build.VERSION.SDK_INT > 24) {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (Build.VERSION.SDK_INT > 18) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        alertDialog.getWindow().setType(type);
        alertDialog.show();
    }

}
