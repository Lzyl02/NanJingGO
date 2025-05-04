package com.COMP3040.NanjingGo.Activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.COMP3040.NanjingGo.R
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.core.StringContains.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for the LoginActivity.
 *
 * These tests verify the functionality of login, password reset, and error handling
 * within the LoginActivity. The scenarios include successful and unsuccessful login attempts
 * as well as password reset functionality.
 */
@RunWith(AndroidJUnit4::class)
class LoginActivityInstrumentTest {

    /**
     * Launches the LoginActivity for testing.
     */
    @get:Rule
    var activityRule = ActivityScenarioRule(LoginActivity::class.java)

    /**
     * Sets up the test environment before each test.
     *
     * Ensures the user is logged out of Firebase Authentication to maintain consistent test conditions.
     */
    @Before
    fun setUp() {
        FirebaseAuth.getInstance().signOut() // Ensure user is logged out before tests
    }

    /**
     * Tests a successful login attempt.
     *
     * Enters valid email and password, simulates a click on the login button,
     * and verifies navigation to the next page by checking the presence of a UI element.
     */
    @Test
    fun testLoginSuccessAndNavigateToNextPage() {
        val email = "12@qq.com"
        val password = "000000"

        // Input email and password
        onView(withId(R.id.login_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.login_password)).perform(typeText(password), closeSoftKeyboard())

        // Perform login
        onView(withId(R.id.login_button)).perform(click())

        // Wait for login to process
        Thread.sleep(2000) // Adjust as necessary

        // Verify navigation to the next page
        onView(withId(R.id.textView2)).check(matches(isDisplayed())) // Replace with a relevant ID on the next screen
    }

    /**
     * Tests a failed login attempt.
     *
     * Enters invalid credentials, simulates a click on the login button,
     * and verifies that a failure message is displayed.
     */
    @Test
    fun testLoginFailure() {
        val email = "invalid@example.com"
        val password = "wrongPassword123"

        // Input email and password
        onView(withId(R.id.login_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.login_password)).perform(typeText(password), closeSoftKeyboard())

        // Perform login
        onView(withId(R.id.login_button)).perform(click())

        // Wait for login to process
        Thread.sleep(2000) // Adjust as necessary

        // Verify the error message
        onView(withId(R.id.login_message)).check(matches(isDisplayed()))
        onView(withId(R.id.login_message)).check(matches(withText(containsString("Login Failed"))))
    }

    /**
     * Tests a successful password reset attempt.
     *
     * Enters a valid email, simulates a click on the "Forgot Password?" button,
     * and verifies that a success message is displayed.
     */
    @Test
    fun testPasswordResetSuccess() {
        val email = "12@qq.com"

        // Input email
        onView(withId(R.id.login_email)).perform(typeText(email), closeSoftKeyboard())

        // Perform password reset
        onView(withId(R.id.forgot_password_button)).perform(click())

        // Wait for reset process
        Thread.sleep(2000) // Adjust as necessary

        // Verify success message
        onView(withId(R.id.reset_password_message)).check(matches(isDisplayed()))
        onView(withId(R.id.reset_password_message)).check(matches(withText("Password reset email sent")))
    }

    /**
     * Tests a failed password reset attempt.
     *
     * Leaves the email field empty, simulates a click on the "Forgot Password?" button,
     * and verifies that an appropriate error message is displayed.
     */
    @Test
    fun testPasswordResetFailure() {
        val email = ""

        // Input email
        onView(withId(R.id.login_email)).perform(typeText(email), closeSoftKeyboard())

        // Perform password reset
        onView(withId(R.id.forgot_password_button)).perform(click())

        // Verify failure message
        onView(withId(R.id.reset_password_message)).check(matches(isDisplayed()))
        onView(withId(R.id.reset_password_message)).check(matches(withText("Please enter your email to reset your password")))
    }
}
