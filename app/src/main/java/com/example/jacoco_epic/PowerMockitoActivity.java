package com.example.jacoco_epic;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.kangkang.util.PowerMockito;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PowerMockitoActivity extends Activity {
    private Handler mHandler = new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doAnswer() {
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                System.out.println("main_button2 - onClick : " + v);
            }
        });
    }
    public void testGAODE(View view) {

    }

//    class MyOnClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            System.out.println("main_button - onClick : " + v);
//        }
//    };
    public void when(String path) {
        System.out.println("shizhikang - when: " + path);
        File file = new File(path);
        if (this.isFinishing()) {
            System.out.println("when - true");
        } else {
            System.out.println("when - false");
        }
    }
    public void whenStatic() {
        System.out.println("shizhikang - whenStatic: ");
        switch (Math.min(1, 2)) {
            case 1:
                System.out.println("whenStatic - 1");
                break;
            case 2:
                System.out.println("whenStatic - 2");
                break;
                default:
                    System.out.println("whenStatic - other");
                    break;
        }
    }
    private boolean myPrivate() {
        return true;
//        return false;
    }

    private void myPrivate2() {
        System.out.println("shizhikang - myPrivate2");
    }
    private void whenPrivate() {
        System.out.println("shizhikang - whenPrivate");
        myPrivate2();
        if (myPrivate()) {
            System.out.println("whenPrivate - true");
        } else {
            System.out.println("whenPrivate - false");
        }
    }

    private void throwException() throws IOException {

    }
    public void whenThrow() {
        try {
            System.out.println("whenThrow - begin");
            throwException();
            System.out.println("whenThrow - end");
        } catch (IOException e) {
            System.out.println("whenThrow - IOException");
        } finally {
        }
    }

//    public void whenNew(Object object) {
//        System.out.println("shizhikang - whenNew: " + object);
//        Thread dbFile = new Thread("test");
//        System.out.println("shizhikang - whenNew - dbFile: " + dbFile);
//        System.out.println("shizhikang - whenNew - dbFile: " + dbFile.hashCode());
//        if (dbFile == object) {
//            System.out.println("whenNew - true");
//        }
//    }

    private void testPrivate() {

    }
    public void verify() {
        testPrivate();
    }
    public void testFinish() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PowerMockitoActivity.this.finish();
            }
        });
    }
    public void testFinal() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        System.out.println("testFinal - format: " + format.format(new Date()));
    }

}
