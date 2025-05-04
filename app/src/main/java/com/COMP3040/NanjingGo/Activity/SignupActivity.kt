package com.COMP3040.NanjingGo.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.COMP3040.NanjingGo.Domain.User
import com.COMP3040.NanjingGo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * Activity for handling user sign-up.
 *
 * This activity provides a sign-up interface for users, allowing them to create an account
 * with email and password. The user's data is saved to Firebase Authentication and the
 * Realtime Database.
 */
class SignupActivity : AppCompatActivity() {

    /**
     * Firebase Authentication instance for handling user authentication.
     */
    lateinit var auth: FirebaseAuth

    /**
     * TextView for displaying messages to the user, such as errors or success confirmations.
     */
    lateinit var messageTextView: TextView

    /**
     * Called when the activity is first created.
     *
     * Initializes the UI, sets up Firebase Authentication, and configures the sign-up process.
     *
     * @param savedInstanceState If the activity is being reinitialized after previously
     * being shut down, this Bundle contains the data it most recently supplied.
     * Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sign Up"

        // Find views
        val emailField = findViewById<EditText>(R.id.signup_email)
        val passwordField = findViewById<EditText>(R.id.signup_password)
        val confirmPasswordField = findViewById<EditText>(R.id.signup_confirm_password)
        val signupButton = findViewById<Button>(R.id.signup_button)
        messageTextView = findViewById(R.id.signup_message)

        // Set up the sign-up button's click listener
        signupButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            // Validate input fields
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                updateMessage("Please fill out all fields")
            } else if (password != confirmPassword) {
                updateMessage("Passwords do not match")
            } else {
                val username = email.substringBefore("@") // Extract username from email

                // Register the user with Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Save user data to the database
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                saveUserToDatabase(userId, username, email)
                            }
                            updateMessage("Signup Successful")
                            finish() // Close the activity
                        } else {
                            updateMessage("Signup Failed: ${task.exception?.message}")
                        }
                    }
            }
        }
    }

    /**
     * Saves the user data to the Firebase Realtime Database.
     *
     * @param userId The unique identifier for the user (from Firebase Authentication).
     * @param username The username of the user, extracted from their email address.
     * @param email The user's email address.
     */
    private fun saveUserToDatabase(userId: String, username: String, email: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val user = User(username = username, email = email, favoriteLocations = emptyList())

        // Save user object to the database under their user ID
        databaseReference.child(userId).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateMessage("User data saved successfully!")
                } else {
                    updateMessage("Failed to save user data: ${task.exception?.message}")
                }
            }
    }

    /**
     * Updates the message displayed in the TextView.
     *
     * @param message The message to display.
     */
    private fun updateMessage(message: String) {
        messageTextView.text = message
    }

    /**
     * Handles the back button in the action bar.
     *
     * Navigates the user back to the previous activity.
     *
     * @return True if the navigation is handled successfully.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
