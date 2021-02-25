/**
 * <p>Project: patac_k226_git</p>
 * <p>Package: com.patac.hmi.engmode.ui.fragment</p>
 * <p>File: MainFragment.java</p>
 * <p>Date: 2016/9/20/14:55.</p>
 */
package com.example.jacoco_epic.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jacoco_epic.R;

public class MainFragment2 extends Fragment {
    public static final String FRAGMENT_NAME = MainFragment2.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.another_right_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        System.out.println("shizhikang - MainFragment2 - getActivity: " + getActivity() );
    }

    public Activity getMyActivity() {
        return getActivity();
    }
}
