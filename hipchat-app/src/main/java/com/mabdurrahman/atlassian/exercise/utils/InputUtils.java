package com.mabdurrahman.atlassian.exercise.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/10/16.
 */
public class InputUtils {

    public static final String TAG = InputUtils.class.getSimpleName();

    public static void hideKeyboard(Activity activity) {
        if (activity == null)
            return;

        try {
            View currentFocus = activity.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void showKeyboard(Activity activity, EditText editText) {
        if (activity == null || editText == null)
            return;

        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
