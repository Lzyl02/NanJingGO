package com.COMP3040.NanjingGo

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.COMP3040.NanjingGo.Activity.TopLocationsActivity
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import androidx.test.espresso.action.ViewActions.click

/**
 * Instrumented test class for `TopLocationsActivity`.
 * Verifies UI behavior, including progress bar visibility, RecyclerView display, and item interactions.
 */
class TopLocationsActivityTest {

    /**
     * Setup method, executed before each test.
     * Used to initialize resources required for testing.
     */
    @Before
    fun setup() {
        // Any setup needed before running the tests
    }

    /**
     * Cleanup method, executed after each test.
     * Used to release resources or reset states.
     */
    @After
    fun tearDown() {
        // Clean up resources after running the tests
    }

    /**
     * Test to verify that the progress bar is displayed initially when the activity is launched.
     */
    @Test
    fun testProgressBarIsDisplayedOnLoad() {
        // Launch the activity
        ActivityScenario.launch(TopLocationsActivity::class.java)

        // Verify the progress bar is displayed
        onView(withId(R.id.progressBarTopLocation)) // Replace with the actual progress bar ID
            .check(matches(isDisplayed()))
    }

    /**
     * Test to ensure that the RecyclerView is displayed after data loading is complete.
     */
    @Test
    fun testRecyclerViewIsDisplayedAfterLoading() {
        // Launch the activity
        ActivityScenario.launch(TopLocationsActivity::class.java)

        // Wait for data to load (for demonstration; use IdlingResource in production)
        Thread.sleep(3000)

        // Verify that the RecyclerView is displayed
        onView(withId(R.id.viewTopLocationList)) // Replace with the actual RecyclerView ID
            .check(matches(isDisplayed()))
    }

    /**
     * Test to verify scrolling and item interactions within the RecyclerView.
     * Simulates user behavior by scrolling and clicking on items.
     */
    @Test
    fun testRecyclerViewScrollAndItemClick() {
        // Launch the activity
        ActivityScenario.launch(TopLocationsActivity::class.java)

        // Wait for data to load (for demonstration; use IdlingResource in production)
        Thread.sleep(3000)

        // Scroll to a specific position in the RecyclerView
        onView(withId(R.id.viewTopLocationList))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10)) // Replace 10 with expected item count

        // Perform a click action on the first item in the RecyclerView
        onView(withId(R.id.viewTopLocationList))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }
}
