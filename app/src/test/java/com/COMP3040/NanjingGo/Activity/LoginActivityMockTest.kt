package com.COMP3040.NanjingGO.Activity

import android.content.Intent
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.COMP3040.NanjingGo.Activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.COMP3040.NanjingGo.R

/**
 * Unit tests for `LoginActivity` using mock Firebase services.
 * These tests verify that login and password reset functionality behaves correctly without interacting with real Firebase services.
 */
class LoginActivityMockTest {

    // Mock FirebaseAuth instance to simulate authentication functionality
    @Mock
    private lateinit var mockAuth: FirebaseAuth

    // Mock AuthResult to simulate Firebase authentication result
    @Mock
    private lateinit var mockAuthResult: AuthResult

    // Mock FirebaseUser to simulate the authenticated user
    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects and defines their behaviors.
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Initialize mocks

        // Mock current user behavior
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.email).thenReturn("testuser@example.com")
    }

    /**
     * Test case for successful login.
     * Verifies that a success message is displayed when the user logs in with correct credentials.
     */
    @Test
    fun testLoginSuccess() {
        // Simulate successful login behavior
        val mockTask: Task<AuthResult> = Tasks.forResult(mockAuthResult)
        `when`(mockAuth.signInWithEmailAndPassword("testuser@example.com", "password123"))
            .thenReturn(mockTask)

        // Launch LoginActivity
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        scenario.onActivity { activity ->
            activity.auth = mockAuth // Inject mocked FirebaseAuth

            // Fill in email and password fields
            val emailField = activity.findViewById<EditText>(R.id.login_email)
            val passwordField = activity.findViewById<EditText>(R.id.login_password)
            val loginButton = activity.findViewById<TextView>(R.id.login_button)

            emailField.setText("testuser@example.com")
            passwordField.setText("password123")

            // Perform login click
            loginButton.performClick()

            // Verify successful login message
            val loginMessage = activity.findViewById<TextView>(R.id.login_message)
            assert(loginMessage.text.toString() == "Login Successful")
        }
    }

    /**
     * Test case for failed login.
     * Verifies that an error message is displayed when login fails due to incorrect credentials.
     */
    @Test
    fun testLoginFailure() {
        // Simulate login failure behavior
        val mockTask: Task<AuthResult> = Tasks.forException(Exception("Invalid credentials"))
        `when`(mockAuth.signInWithEmailAndPassword("testuser@example.com", "wrongpassword"))
            .thenReturn(mockTask)

        // Launch LoginActivity
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        scenario.onActivity { activity ->
            activity.auth = mockAuth // Inject mocked FirebaseAuth

            // Fill in email and password fields
            val emailField = activity.findViewById<EditText>(R.id.login_email)
            val passwordField = activity.findViewById<EditText>(R.id.login_password)
            val loginButton = activity.findViewById<TextView>(R.id.login_button)

            emailField.setText("testuser@example.com")
            passwordField.setText("wrongpassword")

            // Perform login click
            loginButton.performClick()

            // Verify failed login message
            val loginMessage = activity.findViewById<TextView>(R.id.login_message)
            assert(loginMessage.text.toString().contains("Login Failed"))
        }
    }

    /**
     * Test case for password reset functionality.
     * Verifies that a success message is displayed when a password reset email is sent.
     */
    @Test
    fun testPasswordReset() {
        // Simulate successful password reset behavior
        val mockTask: Task<Void> = Tasks.forResult(null)
        `when`(mockAuth.sendPasswordResetEmail("testuser@example.com"))
            .thenReturn(mockTask)

        // Launch LoginActivity
        val scenario = ActivityScenario.launch(LoginActivity::class.java)

        scenario.onActivity { activity ->
            activity.auth = mockAuth // Inject mocked FirebaseAuth

            // Fill in email field
            val emailField = activity.findViewById<EditText>(R.id.login_email)
            val forgotPasswordButton = activity.findViewById<TextView>(R.id.forgot_password_button)

            emailField.setText("testuser@example.com")

            // Perform forgot password click
            forgotPasswordButton.performClick()

            // Verify password reset message
            val resetPasswordMessage = activity.findViewById<TextView>(R.id.reset_password_message)
            assert(resetPasswordMessage.text.toString() == "Password reset email sent")
        }
    }
}
