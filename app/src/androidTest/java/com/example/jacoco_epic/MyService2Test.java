package com.example.jacoco_epic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.jacoco_epic.base.ServiceTest;
import com.example.jacoco_epic.base.util.ThreadUtils;
import com.kangkang.util.PowerMockito;
import com.kangkang.util.ReflectionUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试带Notification/Dialog操作的Service
 * */
public class MyService2Test extends ServiceTest {
    private static final int ONCREATE = 0;
    private static final int TEST_DIALOG = 1;
    private final static List<String> mListString = new ArrayList<>();

    Context mContext;
    private MyService2 mMyService2;
    //主线程handler，注意，测试线程不能操作UI
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ONCREATE:
                    mMyService2.onCreate();
                    break;
                case TEST_DIALOG:
                    test_dialog();
                    break;
                default:
            }
        }
    };

    public MyService2Test() throws Exception {
        super(MyService2.class);
    }

    @Before
    public void setup() throws Exception{
        mContext = getContext();
        mMyService2 = (MyService2) getService();
//        mMyService2 = new MyService2();
//        System.out.println("mMyService2: " + mMyService2);
//        Application application = new Application();
//        Class clazz_ActivityThread = Class.forName("android.app.ActivityThread");
//        Object ActivityManagerNative_getDefault = ReflectionUtils.invokeStaticMethod("android.app.ActivityManagerNative", "getDefault", null, null);
//        //设置Service相关参数
//        ReflectionUtils.invokePrivateMethodAnyway(mMyService2, "attach", new Class[] {Context.class, clazz_ActivityThread, String.class, IBinder.class, Application.class, Object.class}, new Object[] {mContext, null, mMyService2.getClass().toString(), null, application, ActivityManagerNative_getDefault});
//        onCreate();
    }

    @Test
    public void testDialog() throws Exception{
        ThreadUtils.runMainThread(new Runnable() {
            @Override
            public void run() {
                test_dialog();
            }
        });
//        mHandler.sendEmptyMessage(TEST_DIALOG);
//        Thread.sleep(300);
    }
    private void test_dialog() {
        try {
            PowerMockito.doAnswer(AlertDialog.Builder.class, "setPositiveButton", new PowerMockito.Answer() {
                @Override
                public void beforeCall(Object[] args) {
                    //获取setPositiveButton中的参数
                    DialogInterface.OnClickListener listener = (DialogInterface.OnClickListener) args[1];
                    //覆盖DialogInterface.OnClickListener.onClick
                    listener.onClick(null, 0);
                    System.out.println("shizhikang - doAnswer - setPositiveButton");
                }

                @Override
                public void afterCall(Object[] args) {

                }
            }, CharSequence.class, DialogInterface.OnClickListener.class);
            Class clazz_SystemProperties = Class.forName("android.os.SystemProperties");
            int sdk_back = Build.VERSION.SDK_INT;
            //25/17会提示权限拒绝，先屏蔽show
            PowerMockito.whenThenReturn(Dialog.class, "show", null);
            //branch Build.VERSION.SDK_INT > 24
            ReflectionUtils.setStaticField(android.os.Build.VERSION.class, "SDK_INT", 25);
            mMyService2.testDialog();

            //branch Build.VERSION.SDK_INT > 18
            ReflectionUtils.setStaticField(android.os.Build.VERSION.class, "SDK_INT", 19);
            mMyService2.testDialog();

            //branch Build.VERSION.SDK_INT < 18
            ReflectionUtils.setStaticField(android.os.Build.VERSION.class, "SDK_INT", 17);
            mMyService2.testDialog();
            System.out.println("shizhikang - testDialog - end");

            //恢复SDK_INT,防止对后续测试的影响
            ReflectionUtils.setStaticField(android.os.Build.VERSION.class, "SDK_INT", sdk_back);
            PowerMockito.unHook();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void onBind() {
        mMyService2.onBind(null);
    }
}