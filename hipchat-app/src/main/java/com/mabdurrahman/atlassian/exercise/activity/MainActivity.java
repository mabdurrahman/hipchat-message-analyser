package com.mabdurrahman.atlassian.exercise.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.mabdurrahman.atlassian.exercise.R;
import com.mabdurrahman.atlassian.exercise.utils.ValidationUtils;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnEditorAction;

public class MainActivity extends BasicActivity {

    @Bind(R.id.edit_name)
    protected EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentResource() {
        return R.layout.activity_main;
    }

    private boolean validateInput() {
        if (!ValidationUtils.isValidString(nameEditText, getString(R.string.error_name_required))) {
            nameEditText.requestFocus();
            return false;
        }
        return true;
    }

    @OnEditorAction(R.id.edit_name)
    protected boolean onEditorAction(int actionId) {
        onStartBtn();

        return true;
    }

    @OnClick(R.id.btn_start)
    protected void onStartBtn() {
        if (validateInput()) {
            startNextScreen(nameEditText.getText().toString());
        }
    }

    private void startNextScreen(String name) {
        // TODO Implement ChatActivity
    }

}
