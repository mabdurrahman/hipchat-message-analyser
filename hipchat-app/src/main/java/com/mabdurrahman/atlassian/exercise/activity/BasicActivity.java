package com.mabdurrahman.atlassian.exercise.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public abstract class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResource());

        ButterKnife.bind(this);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            onFirstLaunch();
        }
    }

    protected void onFirstLaunch() {
        // Nothing to-do basically
    }

    protected abstract int getContentResource();

    protected boolean hasTranslucentStatusBar() {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // By default we handle the Home button like Back
        // unless it's overridden by child Activity
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
