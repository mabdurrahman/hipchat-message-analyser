package com.mabdurrahman.atlassian.exercise.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.mabdurrahman.atlassian.exercise.R;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public abstract class BasicActivity extends RxAppCompatActivity {

    @Nullable
    @Bind(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResource());

        ButterKnife.bind(this);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (savedInstanceState == null) {
            onFirstLaunch();
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getToolbarTitle());
        }
    }

    protected void onFirstLaunch() {
        // Nothing to-do basically
    }

    protected String getToolbarTitle() {
        return !TextUtils.isEmpty(super.getTitle())? super.getTitle().toString() : getString(R.string.app_name);
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
