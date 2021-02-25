package com.example.jacoco_epic;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;

import com.kangkang.util.ReflectionUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyBroadcastTest {
    private MyBroadcast mMyBroadcast;
    private Context mContext;
    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getTargetContext();
        //广播测试直接创建对象 调用onReceive方法
        mMyBroadcast = new MyBroadcast();
    }
    @Test
    public void onReceive() throws Exception {
        //测试 switch(string)
        String action1 = "ACTION1";
        String action2 = "ACTION2";
        String action3 = "ACTION3";
        String action4 = "ACTION4";

        //测试显式switch
        Intent intent = new Intent();
        intent.setAction(action1);
        mMyBroadcast.onReceive(mContext, intent);
        intent.setAction(action2);
        mMyBroadcast.onReceive(mContext, intent);
        intent.setAction(action3);
        mMyBroadcast.onReceive(mContext, intent);
        intent.setAction(action4);
        mMyBroadcast.onReceive(mContext, intent);

        //实际编译时，switch(string) 会先判断hashcode是否一致，所以分支数量比预期要多
        //测试隐式的if(hashcode) && !string.equal
        intent.setAction(action4);
        //23之前String.hashCode()为变量hashCode,之后为变量hash
        String hashCode = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            hashCode = "hashCode";
        } else {
            hashCode = "hash";
        }
        ReflectionUtils.setPrivateField(action4, hashCode, action1.hashCode());
        mMyBroadcast.onReceive(mContext, intent);

        intent.setAction(action4);
        ReflectionUtils.setPrivateField(action4, hashCode, action2.hashCode());
        mMyBroadcast.onReceive(mContext, intent);

        intent.setAction(action4);
        ReflectionUtils.setPrivateField(action4, hashCode, action3.hashCode());
        mMyBroadcast.onReceive(mContext, intent);

    }
}