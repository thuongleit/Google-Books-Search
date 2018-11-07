package me.thuongle.googlebookssearch.ui.search


import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.google.gson.Gson
import me.thuongle.googlebookssearch.R
import me.thuongle.googlebookssearch.api.BookService
import me.thuongle.googlebookssearch.api.BookServiceImpl
import me.thuongle.googlebookssearch.api.GoogleBook
import me.thuongle.googlebookssearch.api.GoogleVolumeResponse
import me.thuongle.googlebookssearch.model.MutableLiveResult
import me.thuongle.googlebookssearch.model.Result
import me.thuongle.googlebookssearch.repository.BookRepository
import me.thuongle.googlebookssearch.util.*
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@MediumTest
@RunWith(AndroidJUnit4::class)
class SearchActivityTest {

    @Rule
    @JvmField
    var activityTestRule = DisableAnimationActivityTestRule(SearchActivity::class.java)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityTestRule)
    private lateinit var viewModel: SearchViewModel
    private val searchResults = MutableLiveResult<List<GoogleBook>>()

    @Before
    fun init() {
        viewModel = mock()
        val mockRepo = mock<BookRepository>()
        `when`(mockRepo.getService()).thenReturn(BookServiceImpl.create(BookService.NetworkExecutorType.LEGACY))
        `when`(viewModel.repository).thenReturn(mockRepo)
        `when`(viewModel.searchResult).thenReturn(searchResults)

        activityTestRule.activity.setViewModel(viewModel)
    }

    @After
    fun tearDown(){
        reset(viewModel)
    }

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

    @Test
    fun onQueryTextChanged_EnableDisableSearchButton() {
        val edQuery = onView(withId(R.id.ed_query))
        edQuery.perform(typeText("Hello"))
        val btnSearch = onView(withId(R.id.btn_search))
        btnSearch.check(
            matches(
                allOf(
                    isDisplayed(),
                    isClickable()
                )
            )
        )

        edQuery.perform(clearText())
        btnSearch.check(
            matches(
                allOf(
                    isDisplayed(),
                    not(isClickable())
                )
            )
        )
    }

    @Test
    fun performSearchByPressImeActionButton() {
        onView(withId(R.id.ed_query)).perform(
            typeText("foo"),
            pressImeActionButton()
        )
        verify(viewModel).searchBooks("foo")
    }

    @Test
    fun performSearchByPressEnterButton() {
        onView(withId(R.id.ed_query)).perform(
            typeText("foo"),
            pressKey(KeyEvent.KEYCODE_ENTER)
        )
        verify(viewModel).searchBooks("foo")
    }

    @Test
    fun performSearchByPressSearchButton() {
        onView(withId(R.id.ed_query)).perform(typeText("foo"))
        onView(withId(R.id.btn_search)).perform(click())
        verify(viewModel).searchBooks("foo")
    }

    @Test
    fun performSearch_DisplayProgressBarAndNetworkHint() {
        onView(withId(R.id.ed_query)).perform(
            typeText("foo"),
            pressImeActionButton()
        )
        Espresso.closeSoftKeyboard()
        searchResults.postValue(Result.loading(null))
        verify(viewModel).searchBooks("foo")
        onView(withId(R.id.tv_network_hint)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(
                        getString(
                            R.string.executed_network_type_hint,
                            BookService.NetworkExecutorType.LEGACY.toString()
                        )
                    )
                )
            )
        )
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun performSearch_DisplayError() {
        onView(withId(R.id.ed_query)).perform(
            typeText("foo"),
            pressImeActionButton()
        )
        Espresso.closeSoftKeyboard()
        searchResults.postValue(Result.error("404:Error", null))
        verify(viewModel).searchBooks("foo")
        onView(withId(R.id.btn_retry)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_error_message)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText("404:Error")
                )
            )
        )
    }

    @Test
    fun performSearch_DisplayEmptyResult() {
        onView(withId(R.id.ed_query)).perform(
            typeText("foo"),
            pressImeActionButton()
        )
        Espresso.closeSoftKeyboard()
        searchResults.postValue(Result.success(null))
        verify(viewModel).searchBooks("foo")
        onView(withId(R.id.tv_loading_hint)).check(
            matches(
                allOf(
                    isDisplayed(),
                    withText(getString(R.string.empty_search_result, "foo"))
                )
            )
        )
    }

    @Test
    fun performSearch_DisplayResult() {
        onView(withId(R.id.ed_query)).perform(
            typeText("foo"),
            pressImeActionButton()
        )
        Espresso.closeSoftKeyboard()
        searchResults.postValue(
            Result.success(
                Gson().fromJson(
                    getStringFromFile(getInstrumentation().context, "volumes_q=Android.json"),
                    GoogleVolumeResponse::class.java
                ).items
            )
        )
        verify(viewModel).searchBooks("foo")
        val rvBookList = withId(R.id.rv_book_list)
        onView(rvBookList).check(matches(hasItemCount(5)))
        onView(RecyclerViewMatcher(R.id.rv_book_list).atPosition(0))
            .check(
                matches(
                    allOf(
                        hasDescendant(withText("GUI Design for Android Apps")),
                        hasDescendant(withText("Ryan Cohen, Tao Wang"))
                    )
                )
            )
    }

    @Test
    fun retryRequest() {
        onView(withId(R.id.ed_query)).perform(
            typeText("foo"),
            pressImeActionButton()
        )
        Espresso.closeSoftKeyboard()
        searchResults.postValue(Result.error("404:Error", null))
        verify(viewModel).searchBooks("foo")
        onView(withId(R.id.btn_retry)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_retry)).perform(click())
        verify(viewModel).searchBooks("foo")
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
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_network_hint)).check(matches(not(isDisplayed())))
        onView(withId(R.id.btn_retry)).check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_error_message)).check(matches(not(isDisplayed())))
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
