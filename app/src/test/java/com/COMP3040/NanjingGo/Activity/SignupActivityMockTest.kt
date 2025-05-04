package com.COMP3040.NanjingGo

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import com.COMP3040.NanjingGo.Activity.SignupActivity
import com.COMP3040.NanjingGo.Domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult

/**
 * Unit tests for `SignupActivity` using mock Firebase services.
 * These tests ensure proper behavior of signup logic without interacting with real Firebase services.
 */
class SignupActivityMockTest {

    // Mock FirebaseAuth instance to simulate authentication functionality
    @Mock
    private lateinit var mockAuth: FirebaseAuth

    // Mock FirebaseDatabase instance to simulate database interactions
    @Mock
    private lateinit var mockDatabase: FirebaseDatabase

    // Mock DatabaseReference instance to simulate specific database paths
    @Mock
    private lateinit var mockDatabaseReference: DatabaseReference

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects and defines their behaviors.
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this) // Initialize mocks

        // Simulate successful FirebaseAuth user creation
        `when`(mockAuth.createUserWithEmailAndPassword(anyString(), anyString()))
            .thenAnswer {
                Tasks.forResult(null)
            }

        // Mock database reference for user paths
        `when`(mockDatabase.getReference("users")).thenReturn(mockDatabaseReference)
        `when`(mockDatabaseReference.child(anyString())).thenReturn(mockDatabaseReference)
        `when`(mockDatabaseReference.setValue(any(User::class.java))).thenReturn(Tasks.forResult(null))
    }

    /**
     * Test case for successful signup.
     * Verifies that signup succeeds and user data is stored in the database.
     */
    @Test
    fun testSignupSuccess() {
        val scenario = ActivityScenario.launch(SignupActivity::class.java)

        scenario.onActivity { activity ->
            activity.auth = mockAuth // Inject mocked FirebaseAuth
            activity.messageTextView = mock(TextView::class.java) // Mock TextView for messages

            // Simulate user input
            val emailField = activity.findViewById<EditText>(R.id.signup_email)
            val passwordField = activity.findViewById<EditText>(R.id.signup_password)
            val confirmPasswordField = activity.findViewById<EditText>(R.id.signup_confirm_password)
            val signupButton = activity.findViewById<Button>(R.id.signup_button)

            emailField.setText("testuser@example.com")
            passwordField.setText("password123")
            confirmPasswordField.setText("password123")

            // Perform signup click
            signupButton.performClick()

            // Verify success message is displayed
            verify(activity.messageTextView).text = "Signup Successful"

            // Verify that user data is saved to the database
            verify(mockDatabaseReference).setValue(any(User::class.java))
        }
    }

    /**
     * Test case for signup failure.
     * Simulates a FirebaseAuth failure and verifies the error message is displayed.
     */
    @Test
    fun testSignupFailure() {
        `when`(mockAuth.createUserWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(Tasks.forException<AuthResult>(Exception("Signup failed")))

        val scenario = ActivityScenario.launch(SignupActivity::class.java)

        scenario.onActivity { activity ->
            activity.auth = mockAuth // Inject mocked FirebaseAuth
            activity.messageTextView = mock(TextView::class.java) // Mock TextView for messages

            // Simulate user input
            val emailField = activity.findViewById<EditText>(R.id.signup_email)
            val passwordField = activity.findViewById<EditText>(R.id.signup_password)
            val confirmPasswordField = activity.findViewById<EditText>(R.id.signup_confirm_password)
            val signupButton = activity.findViewById<Button>(R.id.signup_button)

            emailField.setText("testuser@example.com")
            passwordField.setText("password123")
            confirmPasswordField.setText("password123")

            // Perform signup click
            signupButton.performClick()

            // Verify error message is displayed
            verify(activity.messageTextView).text = "Signup Failed: Signup failed"
        }
    }

    /**
     * Test case for mismatched passwords.
     * Verifies that the correct error message is displayed when passwords do not match.
     */
    @Test
    fun testPasswordsDoNotMatch() {
        val scenario = ActivityScenario.launch(SignupActivity::class.java)

        scenario.onActivity { activity ->
            activity.messageTextView = mock(TextView::class.java) // Mock TextView for messages

            // Simulate mismatched password input
            val emailField = activity.findViewById<EditText>(R.id.signup_email)
            val passwordField = activity.findViewById<EditText>(R.id.signup_password)
            val confirmPasswordField = activity.findViewById<EditText>(R.id.signup_confirm_password)
            val signupButton = activity.findViewById<Button>(R.id.signup_button)

            emailField.setText("testuser@example.com")
            passwordField.setText("password123")
            confirmPasswordField.setText("wrongpassword")

            // Perform signup click
            signupButton.performClick()

            // Verify password mismatch message
            verify(activity.messageTextView).text = "Passwords do not match"
        }
    }

    /**
     * Test case for empty signup fields.
     * Verifies that an error message is displayed when fields are left empty.
     */
    @Test
    fun testEmptyFields() {
        val scenario = ActivityScenario.launch(SignupActivity::class.java)

        scenario.onActivity { activity ->
            activity.messageTextView = mock(TextView::class.java) // Mock TextView for messages

            // Simulate empty fields
            val signupButton = activity.findViewById<Button>(R.id.signup_button)

            // Perform signup click
            signupButton.performClick()

            // Verify empty fields error message
            verify(activity.messageTextView).text = "Please fill out all fields"
        }
    }
}
