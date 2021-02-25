package com.example.jacoco_epic.model;

import com.example.jacoco_epic.model.MainModel;

import org.junit.Before;
import org.junit.Test;

public class MainModelTest {
    private MainModel mMainModel;
    @Before
    public void setup() {
        mMainModel = new MainModel();
    }
    @Test
    public void getBoolean() {
        mMainModel.getBoolean();
    }
}
