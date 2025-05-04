package com.COMP3040.NanjingGo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import com.COMP3040.NanjingGo.Activity.MainActivity
import com.COMP3040.NanjingGo.Activity.AccountActivity
import com.COMP3040.NanjingGo.Activity.FavoriteLocationListActivity
import com.COMP3040.NanjingGo.Activity.TopLocationsActivity
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Instrumentation tests for `MainActivity`.
 * These tests check the visibility of UI components and the correctness of navigation actions.
 */
class MainActivityInstrumentTest {

    /**
     * Set up method called before each test.
     * Initializes Espresso Intents to intercept and verify navigation actions.
     */
    @Before
    fun setup() {
        Intents.init() // Initialize intents for navigation testing
    }

    /**
     * Tear down method called after each test.
     * Releases Espresso Intents to free up resources.
     */
    @After
    fun tearDown() {
        Intents.release() // Release intents after tests
    }

    /**
     * Test that the welcome message is displayed with the correct default text for a guest user.
     */
    @Test
    fun testWelcomeMessageIsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.textView2)) // Replace `textView2` with the correct ID for the welcome message
            .check(matches(isDisplayed()))
            .check(matches(withText("Hi, Guest"))) // Default text for a guest user
    }

    /**
     * Test that the profile image is visible on the main screen.
     */
    @Test
    fun testProfileImageIsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.profileImageView)) // Replace `profileImageView` with the actual ID
            .check(matches(isDisplayed()))
    }

    /**
     * Test that the banner (ViewPager) and its indicator dots are visible on the main screen.
     */
    @Test
    fun testBannerIsDisplayedAndDotsAreVisible() {
        ActivityScenario.launch(MainActivity::class.java)

        // Check that the ViewPager (banner) is displayed
        onView(withId(R.id.bannerViewPager))
            .check(matches(isDisplayed()))

        // Check that the indicator dots are displayed
        onView(withId(R.id.bannerIndicator))
            .check(matches(isDisplayed()))
    }

    /**
     * Test that the top locations RecyclerView is displayed on the main screen.
     */
    @Test
    fun testTopLocationsRecyclerViewIsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.recyclerViewTopLocation)) // Replace `recyclerViewTopLocation` with the actual ID
            .check(matches(isDisplayed()))
    }

    /**
     * Test navigation to the `TopLocationsActivity` when the top locations button is clicked.
     */
    @Test
    fun testNavigateToTopLocationsActivity() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.locationListTxt)) // Replace `locationListTxt` with the correct ID
            .perform(click())

        // Verify that it navigates to TopLocationsActivity
        Intents.intended(hasComponent(TopLocationsActivity::class.java.name))
    }

    /**
     * Test navigation to the `FavoriteLocationListActivity` when the favorite locations button is clicked.
     */
    @Test
    fun testNavigateToFavoriteLocationsActivity() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.favoriteLocationsButton)) // Replace with the actual ID for the favorite locations button
            .perform(click())

        // Verify that it navigates to FavoriteLocationListActivity
        Intents.intended(hasComponent(FavoriteLocationListActivity::class.java.name))
    }

    /**
     * Test navigation to the `AccountActivity` when the account button is clicked.
     */
    @Test
    fun testNavigateToAccountActivity() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.accountButton)) // Replace with the actual ID for the account button
            .perform(click())

        // Verify that it navigates to AccountActivity
        Intents.intended(hasComponent(AccountActivity::class.java.name))
    }

    /**
     * Test that the Bell VideoView is visible on the main screen.
     */
    @Test
    fun testBellVideoViewIsDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.bellVideoView)) // Replace `bellVideoView` with the correct ID
            .check(matches(isDisplayed()))
    }
}
