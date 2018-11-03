package me.thuongle.googlebookssearch


import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.view.inputmethod.EditorInfo
import me.thuongle.googlebookssearch.ui.search.SearchActivity
import me.thuongle.googlebookssearch.utils.hasItemCount
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SearchActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SearchActivity::class.java)

    @Test
    fun onPressDeviceBack_CloseApp() {
        try {
            Espresso.pressBack()
            fail("Should have thrown NoActivityResumedException")
        } catch (expected: NoActivityResumedException) {
        }
    }

    @Test
    fun onLaunch_ShowDefaultViewState() {
        testDefaultViewState()
    }

    @Test
    fun onLaunchThenRotate_ShowDefaultViewState() {
        with(UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())) {
            // Rotate device to landscape
            setOrientationLeft()
            testDefaultViewState()

            // Rotate device back to portrait
            setOrientationRight()
            testDefaultViewState()
        }
    }

    private fun testDefaultViewState() {
        onView(withId(R.id.ed_query)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(""),
                    hasImeAction(EditorInfo.IME_ACTION_SEARCH),
                    hasFocus(),
                    withHint(R.string.book_title_or_an_url_hint)
                )
            )
        )
        onView(withId(R.id.btn_search)).check(
            matches(
                allOf(
                    isDisplayed(),
                    not(isClickable())
                )
            )
        )
        onView(withId(R.id.tv_loading_hint)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.searching_by_enter_title_or_an_url_hint)
                )
            )
        )
        onView(withId(R.id.progress)).check(
            matches(
                not(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.rv_book_list)).check(
            matches(
                allOf(
                    not(isDisplayed()),
                    hasItemCount(0)
                )
            )
        )
    }
}
