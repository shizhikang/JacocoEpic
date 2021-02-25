package com.example.jacoco_epic.fragment;

import android.app.Activity;
import android.app.FragmentHostCallback;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jacoco_epic.FragmentActivity;
import com.example.jacoco_epic.R;
import com.example.jacoco_epic.base.ActivityTest;
import com.kangkang.util.ReflectionUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainFragment2Test extends ActivityTest {
    private FragmentActivity mFragmentActivity;
    private Context mContext;
    private MainFragment2 mMainFragment2;
    public MainFragment2Test() throws Exception {
        super(FragmentActivity.class);
    }
    @Before
    public void setup() throws Exception {
        mContext = getContext();
        mFragmentActivity = (FragmentActivity) getActivity();

        createFragment();
    }

    private void createFragment() throws Exception {
        //手动绑定frament相关资源
        mMainFragment2 = new MainFragment2();
        FragmentHostCallback<Activity> mHost = (FragmentHostCallback<Activity>) ReflectionUtils.createPrivateInnerClass("android.app.Activity$HostCallbacks", mFragmentActivity);
        ReflectionUtils.setPrivateFieldAnyway(mMainFragment2, "mHost", mHost);

        View view = mMainFragment2.onCreateView(LayoutInflater.from(mFragmentActivity), (ViewGroup) mFragmentActivity.findViewById(R.id.container), null);
        mMainFragment2.onViewCreated(view, null);
//        mFragmentActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                View view = mMainFragment2.onCreateView(LayoutInflater.from(mFragmentActivity), (ViewGroup) mFragmentActivity.findViewById(R.id.container), null);
//                mMainFragment2.onViewCreated(view, null);
//            }
//        });
    }
    @Test
    public void getMyActivity() {
        Assert.assertNotNull(mMainFragment2.getMyActivity());
    }
}