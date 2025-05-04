package com.COMP3040.NanjingGo.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.COMP3040.NanjingGo.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

/**
 * LoginActivity provides functionality for user login, sign-up navigation, and password reset.
 * It integrates with Firebase Authentication for user management and SharedPreferences for
 * remembering user login details.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var forgotPasswordButton: Button
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var rememberEmailCheckBox: CheckBox
    private lateinit var showPasswordToggle: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var loginMessage: TextView
    private lateinit var resetPasswordMessage: TextView

    /**
     * Called when the activity is created. Initializes UI components, sets up Firebase Auth, and handles "Remember Me" functionality.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down, this contains the data it most recently supplied in `onSaveInstanceState`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setupFirebase()
        loadSavedEmail()
        setupListeners()
    }

    private fun initializeViews() {
        emailField = findViewById(R.id.login_email)
        passwordField = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        signupButton = findViewById(R.id.signup_button)
        forgotPasswordButton = findViewById(R.id.forgot_password_button)
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox)
        rememberEmailCheckBox = findViewById(R.id.remember_email_checkbox)
        showPasswordToggle = findViewById(R.id.show_password_toggle)
        progressBar = findViewById(R.id.login_progress)
        loginMessage = findViewById(R.id.login_message)
        resetPasswordMessage = findViewById(R.id.reset_password_message)

        // 设置密码输入框的初始状态
        passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
    }

    private fun loadSavedEmail() {
        val rememberEmail = sharedPreferences.getBoolean("rememberEmail", false)
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        if (rememberEmail || rememberMe) {
            val savedEmail = sharedPreferences.getString("email", "")
            emailField.setText(savedEmail)
            rememberEmailCheckBox.isChecked = rememberEmail
        }

        if (rememberMe) {
            val savedPassword = sharedPreferences.getString("password", "")
            passwordField.setText(savedPassword)
            rememberMeCheckBox.isChecked = true
        }
    }

    private fun setupListeners() {
        // 密码显示切换
        showPasswordToggle.setOnClickListener {
            if (passwordField.transformationMethod is PasswordTransformationMethod) {
                passwordField.transformationMethod = null
                showPasswordToggle.setImageResource(R.drawable.ic_visibility_off)
            } else {
                passwordField.transformationMethod = PasswordTransformationMethod.getInstance()
                showPasswordToggle.setImageResource(R.drawable.ic_visibility)
            }
            passwordField.setSelection(passwordField.text.length)
        }

        // 密码强度检查
        passwordField.addTextChangedListener {
            val password = it.toString()
            if (password.isNotEmpty()) {
                checkPasswordStrength(password)
            }
        }

        // 登录按钮点击
        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }

        // 注册按钮点击
        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // 忘记密码按钮点击
        forgotPasswordButton.setOnClickListener {
            handleForgotPassword()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill out all fields")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address")
            return false
        }
        return true
    }

    private fun performLogin(email: String, password: String) {
        // Check network connectivity first
        if (!isNetworkAvailable()) {
            handleFailedLogin("No internet connection. Please check your network settings.")
            return
        }

        showProgress(true)
        loginMessage.text = "Connecting to Firebase..."
        loginMessage.visibility = View.VISIBLE
        
        // Log authentication attempt
        android.util.Log.d("LoginActivity", "Attempting to login with email: $email")
        
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    android.util.Log.d("LoginActivity", "Login successful for user: ${authResult.user?.uid}")
                    handleSuccessfulLogin(email, password)
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("LoginActivity", "Login failed", exception)
                    val errorMessage = when (exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> 
                            "Invalid email or password"
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException -> 
                            "No account found with this email"
                        is com.google.firebase.FirebaseNetworkException ->
                            "Network error. Please check your connection"
                        else -> "Authentication failed: ${exception.message}"
                    }
                    handleFailedLogin(errorMessage)
                }
                .addOnCompleteListener {
                    showProgress(false)
                }
        } catch (e: Exception) {
            android.util.Log.e("LoginActivity", "Exception during login", e)
            handleFailedLogin("An unexpected error occurred: ${e.message}")
            showProgress(false)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun handleSuccessfulLogin(email: String, password: String) {
        saveLoginPreferences(email, password)
        showSuccess("Login Successful")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun handleFailedLogin(errorMessage: String?) {
        runOnUiThread {
            loginMessage.text = errorMessage ?: "Unknown error"
            loginMessage.setTextColor(resources.getColor(android.R.color.holo_red_light))
            loginMessage.visibility = View.VISIBLE
            showError(errorMessage ?: "Unknown error")
            
            // Log the error for debugging
            android.util.Log.e("LoginActivity", "Login failed: $errorMessage")
        }
    }

    private fun handleForgotPassword() {
        val email = emailField.text.toString()
        if (email.isEmpty()) {
            showError("Please enter your email to reset your password")
            return
        }

        showProgress(true)
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showSuccess("Password reset email sent")
                } else {
                    showError("Failed to send reset email: ${task.exception?.message}")
                }
                showProgress(false)
            }
    }

    private fun saveLoginPreferences(email: String, password: String) {
        val editor = sharedPreferences.edit()
        if (rememberEmailCheckBox.isChecked || rememberMeCheckBox.isChecked) {
            editor.putString("email", email)
            editor.putBoolean("rememberEmail", rememberEmailCheckBox.isChecked)
        } else {
            editor.remove("email")
            editor.remove("rememberEmail")
        }

        if (rememberMeCheckBox.isChecked) {
            editor.putBoolean("rememberMe", true)
            editor.putString("password", password)
        } else {
            editor.remove("password")
            editor.putBoolean("rememberMe", false)
        }
        editor.apply()
    }

    private fun checkPasswordStrength(password: String) {
        val strength = when {
            password.length < 6 -> "Weak"
            password.length < 8 -> "Medium"
            else -> "Strong"
        }
        val color = when (strength) {
            "Weak" -> android.graphics.Color.RED
            "Medium" -> android.graphics.Color.YELLOW
            else -> android.graphics.Color.GREEN
        }
        loginMessage.setTextColor(color)
        loginMessage.text = "Password Strength: $strength"
        loginMessage.visibility = View.VISIBLE
    }

    private fun showProgress(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        loginButton.isEnabled = !show
        signupButton.isEnabled = !show
        forgotPasswordButton.isEnabled = !show
        emailField.isEnabled = !show
        passwordField.isEnabled = !show
        rememberMeCheckBox.isEnabled = !show
        rememberEmailCheckBox.isEnabled = !show
        showPasswordToggle.isEnabled = !show
    }

    private fun showError(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_red_light))
        snackbar.show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(android.R.color.holo_green_light))
            .show()
    }
}
