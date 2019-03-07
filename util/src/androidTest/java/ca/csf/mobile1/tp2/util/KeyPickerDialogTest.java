package ca.csf.mobile1.tp2.util;

import android.support.v7.app.AppCompatActivity;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import ca.csf.mobile1.util.R;
import ca.csf.mobile1.util.view.KeyPickerDialog;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class KeyPickerDialogTest {

    static {
        //Temporary fix for mockito. Please check if it's needed when upgrading mockito.
        System.setProperty("org.mockito.android.target", ApplicationProvider.getApplicationContext().getCacheDir().getPath());
    }

    @Rule
    public final ActivityTestRule<AppCompatActivity> activityRule = new ActivityTestRule<>(AppCompatActivity.class);

    private KeyPickerDialog.ConfirmListener confirmListener;
    private KeyPickerDialog.CancelListener cancelListener;

    @Before
    public void before() {
        confirmListener = mock(KeyPickerDialog.ConfirmListener.class);
        cancelListener = mock(KeyPickerDialog.CancelListener.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateDialogWithZeroLengthKey() {
        KeyPickerDialog.make(activityRule.getActivity(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateDialogWithLessThanZeroLengthKey() {
        KeyPickerDialog.make(activityRule.getActivity(), -1);
    }

    @Test
    public void canOpenDialog() throws Throwable {
        checkDialogIsNotShown();

        show(0);

        checkDialogIsShown();
    }

    @Test
    public void canPerformOkOnDialog() throws Throwable {
        show(0);

        pressOkButton();

        checkDialogIsNotShown();
        checkConfirmListenerIsCalled(0);
    }

    @Test
    public void canPerformCancelOnDialog() throws Throwable {
        show(0);

        pressCancelButton();

        checkDialogIsNotShown();
        checkCancelListenerIsCalled();
    }

    @Test
    public void canSetKeyOnDialog() throws Throwable {
        show(15124);

        pressOkButton();

        checkDialogIsNotShown();
        checkConfirmListenerIsCalled(15124);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetNegativeKey() throws Throwable {
        KeyPickerDialog.make(activityRule.getActivity(), 5)
                .setKey(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotSetKeyLargerThanItsSize() throws Throwable {
        KeyPickerDialog.make(activityRule.getActivity(), 5)
                .setKey(100000);
    }

    //region Test tools

    private void show(int key) throws Throwable {
        activityRule.runOnUiThread(() -> KeyPickerDialog.make(activityRule.getActivity(), 5)
                .setConfirmAction(confirmListener)
                .setCancelAction(cancelListener)
                .setKey(key)
                .show());
    }

    private void pressOkButton() {
        onView(withId(android.R.id.button1)).perform(click());
    }

    private void pressCancelButton() {
        onView(withId(android.R.id.button2)).perform(click());
    }

    private void checkDialogIsNotShown() {
        onView(withId(R.id.keyPickerDialog)).check(doesNotExist());
    }

    private void checkDialogIsShown() {
        onView(withId(R.id.keyPickerDialog)).check(matches(isDisplayed()));
    }

    private void checkConfirmListenerIsCalled(int key) {
        verify(confirmListener).onKeyPickerDialogConfirm(eq(key));
    }

    private void checkCancelListenerIsCalled() {
        verify(cancelListener).onKeyPickerDialogCancel();
    }

    //endregion

}