package com.COMP3040.NanjingGo.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.COMP3040.NanjingGo.databinding.ActivityIntroBinding

/**
 * IntroActivity serves as the welcome screen of the application.
 * It checks the "Remember Me" preference and navigates to the appropriate activity
 * based on the user's previous selection.
 */
class IntroActivity : BaseActivity() {

    private lateinit var binding: ActivityIntroBinding // Binding object for accessing layout views
    private lateinit var sharedPreferences: SharedPreferences // SharedPreferences for saving user preferences

    /**
     * Called when the activity is created. It initializes the UI
     * and checks if the user has selected the "Remember Me" option.
     *
     * @param savedInstanceState Saved instance state, or null if this is a fresh launch.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater) // Inflate the layout using view binding
        setContentView(binding.root) // Set the content view

        // Retrieve shared preferences to check the "Remember Me" status
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        if (rememberMe) {
            // If "Remember Me" is enabled, navigate directly to MainActivity
            goToMainActivity()
        } else {
            // If "Remember Me" is not enabled, set up the Start button
            binding.apply {
                startBtn.setOnClickListener {
                    // Navigate to LoginActivity when the Start button is clicked
                    startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                }
            }
        }
    }

    /**
     * Navigates the user to the MainActivity and finishes the current activity.
     * This is used when the user has enabled the "Remember Me" feature.
     */
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java) // Intent to launch MainActivity
        startActivity(intent) // Start the MainActivity
        finish() // Close the current activity to prevent returning to it
    }
}
