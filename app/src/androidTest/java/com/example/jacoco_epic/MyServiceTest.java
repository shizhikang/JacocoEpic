package com.example.jacoco_epic;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ServiceTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class MyServiceTest extends ServiceTestCase<MyService> {
    Context mContext;
    private MyService mMyService;
    public MyServiceTest() {
        super(MyService.class);
    }

    @Before
//    public void setup()
    public void setup1() {
        mContext = InstrumentationRegistry.getTargetContext();
        setContext(mContext);
        //查看ServiceTestCase源码可知，此时只是创建了一个虚假的Service，并绑定相关参数。并没有调用ams.startService
        //此种方式只能测试没有启动notification和dialog的service，如果需要测试，参考MyService2
        startService(new Intent(mContext, MyService.class));
        mMyService = getService();
    }

    @Test
    public void onStartCommand() {
        Intent intent = null;
        mMyService.onStartCommand(intent, 0, 0);
        intent = new Intent();
        mMyService.onStartCommand(intent, 0, 0);
        intent.putExtra("test", true);
        mMyService.onStartCommand(intent, 0, 0);
    }
    @Test
    public void onBind() {
        mMyService.onBind(null);
    }
}