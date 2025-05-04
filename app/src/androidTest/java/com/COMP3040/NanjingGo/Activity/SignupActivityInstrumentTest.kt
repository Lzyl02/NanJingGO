package com.COMP3040.NanjingGo.Activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.COMP3040.NanjingGo.R
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.core.StringContains.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for the SignupActivity.
 * These tests validate the behavior of the signup process under different conditions.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SignupActivityInstrumentTest {

    /**
     * Launches the `SignupActivity` before each test case.
     */
    @get:Rule
    var activityRule = ActivityScenarioRule(SignupActivity::class.java)

    /**
     * Sets up the testing environment.
     * Ensures that the user is logged out before running any test cases.
     */
    @Before
    fun setUp() {
        FirebaseAuth.getInstance().signOut() // Log out any existing users to ensure clean state
    }

    /**
     * Test case for empty signup fields.
     * Ensures that attempting to sign up with empty fields shows the correct error message.
     */
    @Test
    fun testSignupFieldsEmpty() {
        // Leave all fields empty and click the signup button
        onView(withId(R.id.signup_email)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.signup_password)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.signup_confirm_password)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.signup_button)).perform(click())

        // Wait for the result (adjust or replace with IdlingResource in production)
        Thread.sleep(2000)

        // Verify that the error message for empty fields is displayed
        onView(withId(R.id.signup_message)).check(matches(withText("Please fill out all fields")))
    }

    /**
     * Test case for mismatched passwords.
     * Verifies that entering non-matching passwords displays the correct error message.
     */
    @Test
    fun testPasswordsDoNotMatch() {
        // Enter email and mismatched passwords
        onView(withId(R.id.signup_email)).perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.signup_password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.signup_confirm_password)).perform(typeText("password321"), closeSoftKeyboard())
        onView(withId(R.id.signup_button)).perform(click())

        // Wait for the result (adjust or replace with IdlingResource in production)
        Thread.sleep(2000)

        // Verify that the error message for mismatched passwords is displayed
        onView(withId(R.id.signup_message)).check(matches(withText("Passwords do not match")))
    }

    /**
     * Test case for signup failure due to invalid input.
     * Ensures that attempting to sign up with an invalid email or weak password shows the correct error message.
     */
    @Test
    fun testSignupFailure() {
        // Enter an invalid email and weak password
        onView(withId(R.id.signup_email)).perform(typeText("invalidemail.com"), closeSoftKeyboard())
        onView(withId(R.id.signup_password)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.signup_confirm_password)).perform(typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.signup_button)).perform(click())

        // Wait for the result (adjust or replace with IdlingResource in production)
        Thread.sleep(2000)

        // Verify that the error message for signup failure is displayed
        onView(withId(R.id.signup_message)).check(matches(withText(containsString("Signup Failed"))))
    }
}
