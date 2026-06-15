package com.example.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    // handling the user login attempt when the login button is clicked.
    fun home(view: View) {
        val etUser = findViewById<EditText>(R.id.et_login_username)
        val etEmail = findViewById<EditText>(R.id.et_login_email)
        val etPass = findViewById<EditText>(R.id.et_login_password)

        val inputUser = etUser.text.toString().trim()
        val inputEmail = etEmail.text.toString().trim()
        val inputPass = etPass.text.toString().trim()

        // Check for empty fields.
        if (inputUser.isEmpty()) {
            etUser.error = "Username is required"
            return
        }
        if (inputEmail.isEmpty()) {
            etEmail.error = "Email is required"
            return
        }
        if (inputPass.isEmpty()) {
            etPass.error = "Password is required"
            return
        }

        // Validate formats for consistency.
        if (!inputUser.matches(Regex("^[a-zA-Z]+$"))) {
            etUser.error = "Username should only contain letters"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            etEmail.error = "Please enter a valid email address"
            return
        }

        val passwordPattern = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$")
        if (!inputPass.matches(passwordPattern)) {
            etPass.error = "Password must include letters, numbers, and special characters"
            return
        }

        // retrieving the locally saved registration data for verification.
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val registeredUser = sharedPref.getString("REG_USER", "")
        val registeredEmail = sharedPref.getString("REG_EMAIL", "")
        val registeredPass = sharedPref.getString("REG_PASS", "")

        // Validate against stored credentials.
        if (inputUser == registeredUser && inputEmail == registeredEmail && inputPass == registeredPass) {
            val loadingOverlay = findViewById<View>(R.id.loadingOverlay)
            loadingOverlay.visibility = View.VISIBLE

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                // saving the login state.
                val editor = sharedPref.edit()
                editor.putBoolean("IS_LOGGED_IN", true)
                editor.apply()

                loadingOverlay.visibility = View.GONE
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ThirdActivity::class.java))
                finish()
            }, 600)
        } else {
            // Specific error feedback.
            if (inputUser != registeredUser) {
                etUser.error = "User not found"
            } else {
                etPass.error = "Incorrect password"
            }
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }

    // navigating to the registration page if the user doesn't have an account.
    fun register_page(view: View) {
        val intent = Intent(this, FirstActivity::class.java)
        startActivity(intent)
    }
}
