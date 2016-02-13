package com.mabdurrahman.atlassian.exercise;

import android.support.test.InstrumentationRegistry;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mabdurrahman.atlassian.exercise.activity.MainActivity;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.containsString;

import static com.mabdurrahman.atlassian.exercise.utils.WaitUtils.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/12/16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTests {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testStartWithEmptyNameInput() {

        // Tap "Start" button without adding Name
        // ----------------------
        onView(withId(R.id.btn_start)).perform(click());

        // Verify Validation Error message
        // ----------------------
        String expectedError = InstrumentationRegistry.getTargetContext().getString(R.string.error_name_required);
        onView(withId(R.id.edit_name)).check(matches(hasErrorText(expectedError)));
    }

    @Test
    public void testStartWithProperNameInput() {

        // Fill Name Field with some text and close soft keyboard
        // ----------------------
        onView(withId(R.id.edit_name)).perform(typeText("Mahmoud"), closeSoftKeyboard());

        // Tap "Start" button without adding Name
        // ----------------------
        onView(withId(R.id.btn_start)).perform(click());

        // Now we wait some time
        // ----------------------
        waitTime();

        // Verify we switched to Chat screen with Welcome Message displayed
        // ----------------------
        onView(withId(R.id.recycler)).check(matches(hasDescendant(withText(containsString("Hi Mahmoud,")))));

        // Clean up
        // ----------------------
        cleanupWaitTime();
    }

}
