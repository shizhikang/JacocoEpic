package com.example.jacoco_epic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcast extends BroadcastReceiver {
    private final static String ACTION1 = "ACTION1";
    private final static String ACTION2 = "ACTION2";
    private final static String ACTION3 = "ACTION3";
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION1:
                System.out.println("MyBroadcast - ACTION1");
                break;
            case ACTION2:
                System.out.println("MyBroadcast - ACTION2");
                break;
            case ACTION3:
                System.out.println("MyBroadcast - ACTION3");
                break;
            default:
                System.out.println("MyBroadcast - default");
                break;
        }
    }
}
