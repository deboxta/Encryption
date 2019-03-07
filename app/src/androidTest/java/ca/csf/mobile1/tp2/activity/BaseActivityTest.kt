package ca.csf.mobile1.tp2.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.provider.Settings
import android.view.View
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import ca.csf.mobile1.tp2.R
import ca.csf.mobile1.util.view.KeyPickerDialog
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import okio.Timeout
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.test.KoinTest
import java.io.IOException


/*
*              _______ _______ ______ _   _ _______ _____ ____  _   _    _   _   _   _   _   _   _
*           /\|__   __|__   __|  ____| \ | |__   __|_   _/ __ \| \ | |  | | | | | | | | | | | | | |
*          /  \  | |     | |  | |__  |  \| |  | |    | || |  | |  \| |  | | | | | | | | | | | | | |
*         / /\ \ | |     | |  |  __| | . ` |  | |    | || |  | | . ` |  | | | | | | | | | | | | | |
*        / ____ \| |     | |  | |____| |\  |  | |   _| || |__| | |\  |  |_| |_| |_| |_| |_| |_| |_|
*       /_/    \_\_|     |_|  |______|_| \_|  |_|  |_____\____/|_| \_|  (_) (_) (_) (_) (_) (_) (_)
*
* Toute modification au sein de ce fichier peut avoir de graves conséquenes, telles que, mais sans s'y limiter :
*  - Bogues
*  - Erreurs de compilation
*  - Céphalée de tension
*
* Veuillez donc quitter immédiatement ce fichier et ne plus jamais y revenir.
*
*/

internal open class BaseActivityTest : KoinTest {

    private lateinit var okHttpClient: OkHttpClientMock
    private lateinit var objectMapper: ObjectMapper

    @Before
    fun before() {
        Intents.init()

        okHttpClient = OkHttpClientMock()
        objectMapper = ObjectMapper()

        loadKoinModules(listOf(module {
            single<OkHttpClient>(override = true) { okHttpClient }
            single(override = true) { objectMapper }
        }))
    }

    @After
    fun after() {
        Intents.release()
    }

    protected fun simulateServerResponse(response: String) {
        okHttpClient.simulateResponse = response
    }

    protected fun simulateConnectivityError() {
        okHttpClient.simulateConnectivityError = true
    }

    protected fun simulateServerError() {
        okHttpClient.simulateServerError = true
    }

}

private abstract class CallMock(
    private val request: Request,
    private val responseCode: Int? = null,
    private val responseBody: String? = null
) : Call {

    override fun enqueue(responseCallback: Callback) {}
    override fun isExecuted(): Boolean = true
    override fun timeout(): Timeout? = null
    override fun clone(): Call = this
    override fun isCanceled(): Boolean = false
    override fun cancel() {}
    override fun request(): Request = request
    override fun execute(): Response =
        when {
            responseCode == null || responseBody == null -> throw IOException()
            else -> Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(responseCode)
                .message(responseBody)
                .body(ResponseBody.create(MediaType.parse("application/json"), responseBody))
                .build()
        }
}

private class SuccessCallMock(request: Request, response: String) : CallMock(
    request,
    200,
    response
)

private class ConnectivityErrorCallMock(request: Request) : CallMock(
    request
)

private class ServerErrorCallMock(request: Request) : CallMock(
    request,
    500,
    "{ \"error\" : \"Fatal error.\"}"
)

private class OkHttpClientMock : OkHttpClient() {

    var simulateResponse: String? = null
    var simulateConnectivityError: Boolean = false
    var simulateServerError: Boolean = false

    override fun newCall(request: Request): Call {
        return when {
            simulateResponse != null -> SuccessCallMock(request, simulateResponse!!)
            simulateConnectivityError -> ConnectivityErrorCallMock(request)
            simulateServerError -> ServerErrorCallMock(request)
            else -> super.newCall(request)
        }
    }

}

internal fun ok(): ViewAction {
    return object : ViewAction {
        override fun getConstraints() = isDisplayed()
        override fun getDescription() = "performed OK"
        override fun perform(uiController: UiController, view: View) {
            (view.getTag(R.id.keyPickerDialogTag) as KeyPickerDialog?)?.performOk()
        }
    }
}

internal fun cancel(): ViewAction {
    return object : ViewAction {
        override fun getConstraints() = isDisplayed()
        override fun getDescription() = "performed Cancel"
        override fun perform(uiController: UiController, view: View) {
            (view.getTag(R.id.keyPickerDialogTag) as KeyPickerDialog?)?.performCancel()
        }
    }
}

internal fun setKey(key: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints() = isDisplayed()
        override fun getDescription() = "performed OK"
        override fun perform(uiController: UiController, view: View) {
            (view.getTag(R.id.keyPickerDialogTag) as KeyPickerDialog?)?.setKey(key)
        }
    }
}

internal fun snackbar(): Matcher<View> {
    return withId(android.support.design.R.id.snackbar_text)
}

internal fun snackbarButton(): Matcher<View> {
    return withId(android.support.design.R.id.snackbar_action)
}

internal fun changeOrientationToLandscapeOn(activity: Activity) {
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    closeSoftKeyboard()
}

internal fun changeOrientationToPortraitOn(activity: Activity) {
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    closeSoftKeyboard()
}

internal fun toWifiSettingsActivity(): Matcher<Intent> {
    return hasAction(Settings.ACTION_WIFI_SETTINGS)
}