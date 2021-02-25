package com.example.jacoco_epic;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jacoco_epic.base.ActivityTest;
import com.example.jacoco_epic.base.util.ThreadUtils;
import com.example.jacoco_epic.model.MainModel;
import com.kangkang.util.PowerMockito;
import com.kangkang.util.ReflectionUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends ActivityTest {
    private Context mContext;
    private MainActivity mMainActivity;

    public MainActivityTest() throws Exception {
        super(MainActivity.class);
    }
//    @Rule
//    public ActivityTestRule mActivityTestRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setup() {
        mContext = getContext();
        mMainActivity = (MainActivity) getActivity();
//        mContext = InstrumentationRegistry.getTargetContext();
//        mMainActivity = (MainActivity) mActivityTestRule.getActivity();

    }

    @Test
    public void assertMember() throws Exception{
        //设置私有变量
        ReflectionUtils.setPrivateField(mMainActivity, "mPrvateInt", 1);
        //获取私有变量
         int mPrvateInt = (int) ReflectionUtils.getPrivateField(mMainActivity, "mPrvateInt");
        Assert.assertEquals(mPrvateInt, 1);

        //设置static私有变量
        ReflectionUtils.setStaticField(MainActivity.class, "mPrvateString", "11");
        //获取static私有变量
        String mPrvateString = (String) ReflectionUtils.getStaticField(MainActivity.class, "mPrvateString");
        Assert.assertEquals(mPrvateString, "11");

        //设置final私有变量
        List<String> list = new ArrayList<>();
        list.add("111");
        list.add("222");
        ReflectionUtils.setPrivateField(mMainActivity, "mListString", list);
        //获取final私有变量
        List<String> mListString = (List<String>) ReflectionUtils.getPrivateField(mMainActivity, "mListString");
        Assert.assertTrue(mListString.contains("111"));
    }
    @Test
    public void invokePrivateMethod() throws Exception{
        //调用private method
        ReflectionUtils.invokePrivateMethod(mMainActivity, "addString", new Class[] {String.class}, new Object[] {"invokePrivateMethod"});

        //调用private static method
        List<String> mListString = (List<String>) ReflectionUtils.invokePrivateMethod(mMainActivity, "getString", null, null);
        Assert.assertTrue(mListString.contains("invokePrivateMethod"));
    }

    @Test
    public void invokeInnerClassMethod() throws Exception{
        //创建内部类
        Object object = ReflectionUtils.createPrivateInnerClass("com.example.jacoco_epic.MainActivity$WindowView", mMainActivity);
        //invoke innerClass method
        ReflectionUtils.invokePrivateMethod(object, "method", new Class[] {String.class}, new Object[]{"WindowView"});
        //查看内部类的Parameter
        String mString = (String) ReflectionUtils.getPrivateField(object, "mString");
        Assert.assertEquals(mString, "WindowView");

        //创建有参内部类
        Object object_WindowViewWithParam = ReflectionUtils.createPrivateInnerClass("com.example.jacoco_epic.MainActivity$WindowViewWithParam", mMainActivity, "WindowViewWithParam");
        //查看内部类的Parameter
        String mString_WindowViewWithParam = (String) ReflectionUtils.getPrivateField(object_WindowViewWithParam, "mString");
        Assert.assertEquals(mString_WindowViewWithParam, "WindowViewWithParam");
        //invoke innerClass method
        ReflectionUtils.invokePrivateMethod(object_WindowViewWithParam, "method", new Class[] {String.class}, new Object[]{"WindowViewWithParam1"});
        mString_WindowViewWithParam = (String) ReflectionUtils.getPrivateField(object_WindowViewWithParam, "mString");
        //查看内部类的Parameter
        Assert.assertEquals(mString_WindowViewWithParam, "WindowViewWithParam1");

        //创建static内部类
        Object object_WindowViewStatic = ReflectionUtils.createPrivateInnerClass("com.example.jacoco_epic.MainActivity$WindowViewStatic", "WindowViewStatic");
        //查看内部类的Parameter
        String mString_WindowViewStatic = (String) ReflectionUtils.getPrivateField(object_WindowViewStatic, "mString");
        Assert.assertEquals(mString_WindowViewStatic, "WindowViewStatic");
        //invoke innerClass method
        ReflectionUtils.invokePrivateMethod(object_WindowViewStatic, "method", new Class[] {String.class}, new Object[]{"WindowViewStatic1"});
        mString_WindowViewStatic = (String) ReflectionUtils.getPrivateField(object_WindowViewStatic, "mString");
        //查看内部类的Parameter
        Assert.assertEquals(mString_WindowViewStatic, "WindowViewStatic1");

    }
    @Test
    public void callModelMethod() throws Exception{
        MyMainModel myMainModel = new MyMainModel();
        myMainModel.getBoolean = false;
        //反射将MyMainModel设置到MainActivity中,hook原有model
        ReflectionUtils.setPrivateField(mMainActivity, "mMainModel", myMainModel);
        mMainActivity.callModelMethod();

        myMainModel.getBoolean = true;
        mMainActivity.callModelMethod();

    }
    private class MyMainModel extends MainModel {
        public boolean getBoolean = false;
        @Override
        public boolean getBoolean() {
            return getBoolean;
        }
    }
    @Test
    public void testMyAdapter() throws Exception{
        //拿到MyAdapter对象
        ListView listView = (ListView) ReflectionUtils.getPrivateField(mMainActivity, "mList");
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        //或
//        ArrayAdapter<String> adapter = ReflectionUtils.createPrivateInnerClass("com.example.jacoco_epic.MainActivity$MyAdapter", mMainActivity, mMainActivity, android.R.layout.simple_list_item_1, new String[] {"11","22"});
        View view = adapter.getView(0, null, (ViewGroup) listView.findViewById(android.R.layout.simple_list_item_1));
        adapter.getView(0, view, listView);
    }
    @Test
    public void test() throws Exception{
        ThreadUtils.runMainThread(new Runnable() {
            @Override
            public void run() {
                PowerMockito.whenThenReturn(TextView.class, "setText", null, CharSequence.class);
                mMainActivity.test();
                PowerMockito.unHook();
            }
        });
    }
}