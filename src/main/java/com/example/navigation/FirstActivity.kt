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
//import this , put them at the top of the code
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread


class FirstActivity : AppCompatActivity() {

    // this runs when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_first)

        // adding padding to the main view so it doesn't get covered by the system status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // checking if the user is already logged into the app
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("IS_LOGGED_IN", false)

        if (isLoggedIn) {
            // go straight to the dashboard if already logged in
            startActivity(Intent(this, ThirdActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_first)

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // this function handles the registration process when the button is clicked
    fun home(view: View) {

        val username = findViewById<EditText>(R.id.et_username).text.toString().trim()
        val email = findViewById<EditText>(R.id.et_email).text.toString().trim()
        val password = findViewById<EditText>(R.id.et_password).text.toString().trim()
        val confirmPassword = findViewById<EditText>(R.id.et_confirm_password).text.toString().trim()

        // make sure all the input fields are filled in
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // check if both passwords entered are the same
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // clearing old data when a new user registers
        getSharedPreferences("FinancePrefs", MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("UserData", MODE_PRIVATE).edit().clear().apply()

        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()

        // saving the user registration details locally
        editor.putString("REG_USER", username)
        editor.putString("REG_EMAIL", email)
        editor.putString("REG_PASS", password)
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.apply()

        // sending the registration data to the online database
        thread {
            val rowData = mapOf(

                "username" to username,
                "email" to email,
                "xp" to 0,
                "streak" to 0,
                "last_login" to "",
                "password" to password,
            )

            val response = insertRow(
                tableName = "user_ca2c2d3e_Users",
                data = rowData
            )

            // printing the server response for debugging
            runOnUiThread {
                println(response)
            }
        }

        // notifying the user that they have registered successfully
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

        // redirecting the user to the dashboard
        startActivity(Intent(this, ThirdActivity::class.java))
    }

    // this function connects to the php api to insert a new row in the database
    fun insertRow(
        tableName: String,
        data: Map< String, Any?>
    ): String? {
        val url = URL("https://studyplugtools.cloud/you_connect.php/$tableName/insert")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true

            // creating the json object from the data map
            val payload = JSONObject()
            for ((key, value) in data) {
                if (key != "id") {
                    payload.put(key, value ?: "")
                }
            }

            // sending the json data through the connection
            connection.outputStream.use { os ->
                os.write(payload.toString().toByteArray(Charsets.UTF_8))
                os.flush()
            }

            // reading the response coming back from the server
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()
            response.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection.disconnect()
        }
    }

    // switching to the login page
    fun login_page(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }
}
