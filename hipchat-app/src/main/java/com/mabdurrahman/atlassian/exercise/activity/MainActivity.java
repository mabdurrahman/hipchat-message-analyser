package com.mabdurrahman.atlassian.exercise.activity;

import android.os.Bundle;

import com.mabdurrahman.atlassian.exercise.R;

public class MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentResource() {
        return R.layout.activity_main;
    }
}
