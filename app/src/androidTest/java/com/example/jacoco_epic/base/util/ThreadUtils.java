package com.example.jacoco_epic.base.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ThreadUtils {
    private static volatile Object mLock = new Object();
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            synchronized (mLock) {
                super.handleMessage(msg);
            }
        }
    };
    static boolean flag = false;

    public static synchronized void runMainThread(final Runnable runnable) {
        flag = false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mLock) {
                    runnable.run();
                    flag = true;
                }
            }
        });
        waitMainThread();
    }

    public static synchronized void waitMainThread() {
        while (!flag) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void runCurrentThread(Runnable runnable) {
        synchronized (mLock) {
            runnable.run();
        }
    }
}
