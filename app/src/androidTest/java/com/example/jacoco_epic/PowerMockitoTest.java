package com.example.jacoco_epic;

import android.app.Activity;
import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.example.jacoco_epic.base.ActivityTest;
import com.kangkang.util.PowerMockito;
import com.kangkang.util.ReflectionUtils;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;


@RunWith(AndroidJUnit4.class)
public class PowerMockitoTest extends ActivityTest {
//    @Rule
//    public ActivityTestRule<PowerMockitoActivity> mActivityTestRule = new ActivityTestRule<>(PowerMockitoActivity.class);
    private Context mContext;
    private PowerMockitoActivity mPowerMockitoActivity;

    public PowerMockitoTest() throws Exception {
        super(PowerMockitoActivity.class);
    }

    @Before
    public void setUp() throws Exception {
//        mContext = InstrumentationRegistry.getTargetContext();

//        mPowerMockitoActivity = mActivityTestRule.getActivity();
        mContext = getContext();
        mPowerMockitoActivity = (PowerMockitoActivity) getActivity();
    }
    @Test
    public void test() throws Exception{

    }
    @Test
    public void test_when() throws Exception{
        PowerMockito.whenThenReturn(PowerMockitoActivity.class, "isFinishing", true);
        mPowerMockitoActivity.when("111");
//        PowerMockito.unHook(File.class);

        PowerMockito.whenThenReturn(PowerMockitoActivity.class, "isFinishing", false);
        mPowerMockitoActivity.when("111");
        PowerMockito.unHook(Activity.class);
    }

    @Test
    public void test_whenStatic() {
        PowerMockito.whenThenReturn(Math.class, "min", 1, int.class, int.class);
        mPowerMockitoActivity.whenStatic();
//        PowerMockito.unHook();

        PowerMockito.whenThenReturn(Math.class, "min", 2, int.class, int.class);
        mPowerMockitoActivity.whenStatic();
//        PowerMockito.unHook();

        PowerMockito.whenThenReturn(Math.class, "min", 3, int.class, int.class);
        mPowerMockitoActivity.whenStatic();
//        PowerMockito.unHook();
    }

    @Test
    public void test_whenPrivate() throws Exception {
        ReflectionUtils.invokePrivateMethodAnyway(mPowerMockitoActivity, "myPrivate", null, null);
        ReflectionUtils.invokePrivateMethodAnyway(mPowerMockitoActivity, "myPrivate2", null, null);

        System.out.println("shizhikang - test_whenPrivate");
        PowerMockito.whenThenReturn(PowerMockitoActivity.class, "myPrivate2", null);
        PowerMockito.whenThenReturn(PowerMockitoActivity.class, "myPrivate", true);
        ReflectionUtils.invokePrivateMethodAnyway(mPowerMockitoActivity, "whenPrivate", null, null);
//        mPowerMockitoActivity.whenPrivate();
//        PowerMockito.unHook(PowerMockitoActivity.class);

        PowerMockito.whenThenReturn(PowerMockitoActivity.class, "myPrivate", false);
//        mPowerMockitoActivity.whenPrivate();
        ReflectionUtils.invokePrivateMethodAnyway(mPowerMockitoActivity, "whenPrivate", null, null);

        System.out.println("shizhikang - test_whenPrivate - end: " + PowerMockitoActivity.class.getDeclaredMethod("myPrivate"));

//        PowerMockito.unHook(PowerMockitoActivity.class);
    }

    @Test
    public void test_whenThrow() throws Exception {
        //branch dbFile.exists()
        mPowerMockitoActivity.whenThrow();
//        PowerMockito.unHook(File.class);
        try {
            //branch catch IOException
            PowerMockito.whenThrow(PowerMockitoActivity.class, "throwException", new IOException("TestIOException"));
            mPowerMockitoActivity.whenThrow();
//            PowerMockito.unHook(File.class);

            //branch uncatch Exception
            PowerMockito.whenThrow(PowerMockitoActivity.class, "throwException", new JSONException("JSONException"));
            mPowerMockitoActivity.whenThrow();
        } catch (Throwable e) {
            e.printStackTrace();
        }
//        PowerMockito.unHook(File.class);

        //branch try
        PowerMockito.unHook(File.class);
    }
    //todo 无法改变构造的对象
    @Test
    public void test_whenNew() {
//        Thread file = new Thread("test2");
//        PowerMockito.whenNew(Thread.class, file);
//        mPowerMockitoActivity.whenNew(file);
//        PowerMockito.unHook();
//
//        mPowerMockitoActivity.whenNew(file);
    }

    @Test
    public void test_verify() throws Exception {
//        PowerMockito.doAnswer(PowerMockitoActivity.class, "testPrivate", new PowerMockito.Answer() {
//            @Override
//            public void beforeCall(Object[] args) {
//                System.out.println("test_verify - beforeCall");
//            }
//
//            @Override
//            public void afterCall(Object[] args) {
//
//            }
//        });

        PowerMockito.MockObject object = PowerMockito.mock(PowerMockitoActivity.class, new Method[] {PowerMockitoActivity.class.getDeclaredMethod("testPrivate")});
        mPowerMockitoActivity.verify();
        System.out.println("object.verify(\"testPrivate\"):" + object.verify("testPrivate"));
        Assert.assertEquals(object.verify("testPrivate"), 1);
//        PowerMockito.unHook(PowerMockitoActivity.class);
    }

    @Test
    public void test_doAnswer() throws Exception {
//        PowerMockito.whenThenReturn(View.class, "setOnClickListener", null, View.OnClickListener.class);
        //预先设置调用View.setOnClickListener 时拿到其中的参数
        PowerMockito.doAnswer(View.class, "setOnClickListener", new PowerMockito.Answer() {
            @Override
            public void beforeCall(Object[] args) {
                //获取到setOnClickListener中的参数callback，测试callback
                System.out.println("doAnswer - beforeCall : " + args[0]);
                ((View.OnClickListener)args[0]).onClick(null);
            }

            @Override
            public void afterCall(Object[] args) {
            }
        }, View.OnClickListener.class);
        //此方法中的setOnClickListener 会触发doAnswer回调
        mPowerMockitoActivity.doAnswer();
        //解除mock,防止对其它正常用例的影响
//        PowerMockito.unHook();
    }
    @Test
    public void test_finish() throws Exception {
        //45s
        mPowerMockitoActivity.testFinish();
        Thread.sleep(50);
    }
    @Test
    public void test_myPrivate() throws Exception {
        ReflectionUtils.invokePrivateMethod(mPowerMockitoActivity, "myPrivate", null, null);
    }
    @Test
    public void test_testGAODE() throws Exception {
        ReflectionUtils.invokePrivateMethod(mPowerMockitoActivity, "testGAODE", new Class[] {View.class}, new Object[] {null});
    }

    @Test
    public void test_testFinal() throws Exception {
        PowerMockito.whenThenReturn(DateFormat.class, "format", "nihao", Date.class);
        mPowerMockitoActivity.testFinal();
        PowerMockito.whenThenReturn(DateFormat.class, "format", "nihao111", Date.class);
        mPowerMockitoActivity.testFinal();
    }

    @Test
    public void test_getTime() throws Exception {
//        PowerMockito.whenThenReturn(Date.class, "getTime", System.currentTimeMillis());
//        Thread.sleep(1000);
//        PowerMockito.unHook();
    }
}
