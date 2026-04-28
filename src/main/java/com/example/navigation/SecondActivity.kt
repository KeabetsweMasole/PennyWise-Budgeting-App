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

    // initializing the login screen and applying window insets for better layout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // this function handles the user login attempt when the login button is clicked
    fun home(view: View) {

        val inputUser = findViewById<EditText>(R.id.et_login_username).text.toString().trim()
        val inputPass = findViewById<EditText>(R.id.et_login_password).text.toString().trim()

        // checking if the user left any of the login fields empty
        if (inputUser.isEmpty() || inputPass.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            return
        }

        // retrieving the locally saved registration data for verification
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val registeredUser = sharedPref.getString("REG_USER", "")
        val registeredPass = sharedPref.getString("REG_PASS", "")

        // validating the entered credentials against the stored ones
        if (inputUser == registeredUser && inputPass == registeredPass) {

            // saving the login state so the user doesn't have to log in again next time
            val editor = sharedPref.edit()
            editor.putBoolean("IS_LOGGED_IN", true)
            editor.apply()

            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

            // moving the user to the dashboard screen
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)

            // finishing this activity so the user cannot navigate back to login with the back button
            finish() 

        } else {
            // showing an error message if the credentials do not match
            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
        }
    }

    // navigating to the registration page if the user doesn't have an account
    fun register_page(view: View) {
        val intent = Intent(this, FirstActivity::class.java)
        startActivity(intent)
    }
}
