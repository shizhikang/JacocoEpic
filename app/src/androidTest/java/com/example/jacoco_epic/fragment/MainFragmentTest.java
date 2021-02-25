package com.example.jacoco_epic.fragment;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import com.example.jacoco_epic.FragmentActivity;
import com.example.jacoco_epic.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainFragmentTest {
    private FragmentActivity mFragmentActivity;
    private Context mContext;
    private MainFragment mMainFragment;
    @Rule
    public ActivityTestRule mActivityTestRule = new ActivityTestRule(FragmentActivity.class);
    @Before
    public void setup() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        mFragmentActivity = (FragmentActivity) mActivityTestRule.getActivity();

        createFragment();
    }

    private void createFragment() throws Exception {
        //通过fragmentmanager创建fragment
        mMainFragment = new MainFragment();
        mFragmentActivity.getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mMainFragment)//设置显示fragment
                .addToBackStack(null)
                .commit();//此时只是通知AMS去调用相关fragment相关生命周期，并绑定activity
        Assert.assertNull(mMainFragment.getMyActivity());//涉及跨进程通信，此时AMS并没有执行完相关操作
        Thread.sleep(200);
        Assert.assertNotNull(mMainFragment.getMyActivity());//等待200ms左右，正常fragment生命周期应该已经完成
    }
    @Test
    public void getMyActivity() {
        Assert.assertNotNull(mMainFragment.getMyActivity());
    }
}