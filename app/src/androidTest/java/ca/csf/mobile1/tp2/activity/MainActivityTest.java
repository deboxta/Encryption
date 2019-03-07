package ca.csf.mobile1.tp2.activity;

import android.app.Instrumentation;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import ca.csf.mobile1.tp2.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static ca.csf.mobile1.tp2.activity.BaseActivityTestKt.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
@SuppressWarnings("SameParameterValue")
public class MainActivityTest extends BaseActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Test
    public void canSeeKeyId() {
        simulateServerResponseForKey(15547);
        show();

        changeKeyIdTo(15547);

        checkKeyIdIs(15547);
    }

    @Test
    public void canChangeKeyId() {
        simulateServerResponseForKey(15547);
        show();

        openKeyPickerDialog();
        typeInPickerDialog(15547);
        closeKeyPickerDialog();

        checkKeyIdIs(15547);
    }

    @Test
    public void canEncryptText() {
        simulateServerResponseForKey(15547);
        show();

        changeKeyIdTo(15547);
        changeInputTextTo("Bonjour madame.");
        pressEncryptButton();

        checkOutputIs("uGqvGhLUSNaNSIi");
    }

    @Test
    public void canDecryptText() {
        simulateServerResponseForKey(15547);
        show();

        changeKeyIdTo(15547);
        changeInputTextTo("uGqvGhLUSNaNSIi");
        pressDecryptButton();

        checkOutputIs("Bonjour madame.");
    }

    @Test
    public void canCopyOutputTextToClipboard() {
        simulateServerResponseForKey(15547);
        show();

        changeKeyIdTo(15547);
        changeInputTextTo("Bonjour madame.");
        pressEncryptButton();
        pressCopyButton();

        checkClipboardIs("uGqvGhLUSNaNSIi");
        checkSnackbarMessageIs(R.string.text_copied_output);
    }

    @Test
    public void keyPickerDialogIsShownWhenReceivingTextFromOtherApplication() {
        simulateServerResponseForKey(15547);
        show("uGqvGhLUSNaNSIi");

        checkKeyPickerDialogIsOpen();
    }

    @Test
    public void canEncryptTextFromOtherApplication() {
        simulateServerResponseForKey(15547);
        show("Bonjour madame.");

        //KeyPickerDialog should be opened. No need to open it.
        typeInPickerDialog(15547);
        closeKeyPickerDialog();
        pressEncryptButton();

        checkOutputIs("uGqvGhLUSNaNSIi");
    }

    @Test
    public void canDecryptTextFromOtherApplication() {
        simulateServerResponseForKey(15547);
        show("uGqvGhLUSNaNSIi");

        //KeyPickerDialog should be opened. No need to open it.
        typeInPickerDialog(15547);
        closeKeyPickerDialog();
        pressDecryptButton();

        checkOutputIs("Bonjour madame.");
    }

    @Test
    public void cantTypeUnsupportedCharacters() {
        simulateServerResponseForKey(15547);
        show();

        changeInputTextTo("?@!A%$&l#<)l(,:o-;_");

        checkInputIs("Allo");
    }

    @Test
    public void orientationChangeKeepsData() {
        simulateServerResponseForKey(15547);
        show();

        changeKeyIdTo(15547);
        changeInputTextTo("Bonjour madame.");
        pressEncryptButton();

        rotateToLandscape();
        checkKeyIdIs(15547);
        checkInputIs("Bonjour madame.");
        checkOutputIs("uGqvGhLUSNaNSIi");

        rotateToPortrait();
        checkKeyIdIs(15547);
        checkInputIs("Bonjour madame.");
        checkOutputIs("uGqvGhLUSNaNSIi");
    }

    @Test
    public void orientationChangeKeepsKeyPickerDialogOpen() {
        simulateServerResponseForKey(15547);
        show();

        openKeyPickerDialog();

        rotateToLandscape();
        checkKeyPickerDialogIsOpen();

        rotateToPortrait();
        checkKeyPickerDialogIsOpen();
    }

    @Test
    public void showConnectivityErrorMessageIfNoWifi() {
        simulateConnectivityError();
        show();

        checkSnackbarMessageIs(R.string.text_connectivity_error);
    }

    @Test
    public void showServerErrorMessageIfServerResponseIsInvalid() {
        simulateServerError();
        show();

        checkSnackbarMessageIs(R.string.text_server_error);
    }

    @Test
    public void pressingActivateWifiButtonOpenWifiSettings() {
        simulateConnectivityError();
        show();

        pressActivateWifiButton();

        checkWifiSettingsIsOpen();
    }

    @Test
    public void cantEncryptOrDecryptAfterKeyFetchingFailure() {
        simulateConnectivityError();
        show();

        changeInputTextTo("Bonjour madame.");

        pressEncryptButton();
        checkOutputIs("");
        pressDecryptButton();
        checkOutputIs("");
    }

    @Test
    public void whenReceiveTextFromOtherApplicationIfKeyPickerDialogIsCanceledThenActivityIsFinished() {
        simulateServerResponseForKey(15547);
        show("uGqvGhLUSNaNSIi");

        cancelKeyPickerDialog();

        checkActivityIsFinished();
    }

    //region Test tools

    //region Show

    private void show() {
        show(null);
    }

    private void show(String textToDecrypt) {
        Intent intent = null;

        if (textToDecrypt != null) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, textToDecrypt);

        }

        activityRule.launchActivity(intent);

        rotateToPortrait();
    }

    //endregion

    //region Key

    private void openKeyPickerDialog() {
        onView(withId(R.id.selectKeyButton)).perform(click());
    }

    private void typeInPickerDialog(int key) {
        simulateServerResponseForKey(key);
        onView(withId(R.id.keyPickerDialog)).perform(setKey(key));
    }

    private void cancelKeyPickerDialog() {
        onView(withId(R.id.keyPickerDialog)).perform(cancel());
    }

    private void closeKeyPickerDialog() {
        onView(withId(R.id.keyPickerDialog)).perform(ok());
    }

    private void checkKeyPickerDialogIsOpen() {
        onView(withId(R.id.keyPickerDialog)).check(matches(isDisplayed()));
    }

    private void simulateServerResponseForKey(int key) {
        simulateServerResponse("{\"id\":" + key + ",\"outputCharacters\":\"NOCaIKXEFvMtSqGcfLZghjob pQuBTrR.DxesmnkdlPWHJYwyVzAUi\",\"inputCharacters\":\"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ .\"}");
    }

    private void changeKeyIdTo(int key) {
        openKeyPickerDialog();
        typeInPickerDialog(key);
        closeKeyPickerDialog();
    }

    private void checkKeyIdIs(int key) {
        String keyText = activityRule.getActivity().getResources().getString(R.string.text_current_key);
        onView(withId(R.id.currentKeyTextView)).check(matches(withText(String.format(keyText, key))));
    }

    //endregion

    //region Cypher

    private void changeInputTextTo(String text) {
        onView(withId(R.id.inputEditText)).perform(clearText(), typeText(text), replaceText(text));
        closeSoftKeyboard();
    }

    private void checkInputIs(String text) {
        onView(withId(R.id.inputEditText)).check(matches(withText(text)));
    }

    private void checkOutputIs(String text) {
        onView(withId(R.id.outputTextView)).check(matches(withText(text)));
    }

    private void pressEncryptButton() {
        onView(withId(R.id.encryptButton)).perform(click());
    }

    private void pressDecryptButton() {
        onView(withId(R.id.decryptButton)).perform(click());
    }

    private void pressCopyButton() {
        onView(withId(R.id.copyButton)).perform(click());
    }

    //endregion

    //region Snackbar

    private void checkSnackbarMessageIs(@StringRes int resourceId) {
        onView(snackbar()).check(matches(isDisplayed()));
        onView(snackbar()).check(matches(withText(resourceId)));
    }

    private void pressActivateWifiButton() {
        intending(toWifiSettingsActivity()).respondWith(new Instrumentation.ActivityResult(0, new Intent()));
        onView(snackbarButton()).perform(click());
    }

    //endregion

    //region Screen

    private void rotateToLandscape() {
        changeOrientationToLandscapeOn(activityRule.getActivity());
    }

    private void rotateToPortrait() {
        changeOrientationToPortraitOn(activityRule.getActivity());
    }

    //endregion

    //region Others

    private void checkActivityIsFinished() {
        //Wait a bit for the application to close
        SystemClock.sleep(5000);
        assertTrue(activityRule.getActivity().isFinishing());
    }

    @SuppressWarnings("ConstantConditions")
    private void checkClipboardIs(String text) {
        ClipboardManager clipboard = (ClipboardManager) activityRule.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        assertEquals(text, clipboard.getPrimaryClip().getItemAt(0).getText().toString());
    }

    private void checkWifiSettingsIsOpen() {
        intended(toWifiSettingsActivity());
    }

    //endregion

    //endregion

}