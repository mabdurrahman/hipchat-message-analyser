package com.mabdurrahman.atlassian.exercise;

import android.app.Application;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class HipChatApp extends Application {

    public static final String TAG = HipChatApp.class.getSimpleName();

    private static HipChatApp sInstance;

    public HipChatApp() {
        sInstance = this;
    }

    public static HipChatApp getInstance() {
        return sInstance;
    }
}
