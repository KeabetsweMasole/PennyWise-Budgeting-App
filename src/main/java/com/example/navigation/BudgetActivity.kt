package com.example.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class BudgetActivity : AppCompatActivity() {

    private fun formatCurrency(amount: Float): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        symbols.groupingSeparator = ' '
        symbols.decimalSeparator = ','
        val df = DecimalFormat("#,##0.00", symbols)
        return df.format(amount)
    }

    // this is where the budget screen is initialized and values are loaded from storage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget)

        // adding padding to the main view so it doesn't get covered by the system status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etPocket = findViewById<EditText>(R.id.etPocket)
        val etMinBudget = findViewById<EditText>(R.id.etMinBudget)
        val etMaxBudget = findViewById<EditText>(R.id.etMaxBudget)
        val btnSaveBudget = findViewById<Button>(R.id.btnSaveBudget)

        val pref = getSharedPreferences("FinancePrefs", MODE_PRIVATE)

        // loading existing budget values from preferences if they exist
        val currentIncome = pref.getFloat("BUDGET", 0f)
        val currentMin = pref.getFloat("MIN_BUDGET", 0f)
        val currentMax = pref.getFloat("MAX_BUDGET", 0f)

        // pre-filling the input fields with the current saved values
        if (currentIncome > 0) etPocket.setText(formatCurrency(currentIncome))
        if (currentMin > 0) etMinBudget.setText(formatCurrency(currentMin))
        if (currentMax > 0) etMaxBudget.setText(formatCurrency(currentMax))

        // adding listeners to clean the text (removing spaces) when user starts editing
        val editTexts = listOf(etPocket, etMinBudget, etMaxBudget)
        for (et in editTexts) {
            et.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    val currentVal = et.text.toString().replace(" ", "").replace(",", ".")
                    et.setText(currentVal)
                }
            }
        }

        // saving the new budget goals when the user clicks the save button
        btnSaveBudget.setOnClickListener {
            val incomeStr = etPocket.text.toString().trim().replace(" ", "").replace(",", ".")
            val minStr = etMinBudget.text.toString().trim().replace(" ", "").replace(",", ".")
            val maxStr = etMaxBudget.text.toString().trim().replace(" ", "").replace(",", ".")

            // ensuring the income field is not empty before saving
            if (incomeStr.isEmpty()) {
                Toast.makeText(this, "Please enter your income", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val income = incomeStr.toFloat()
            val minBudget = if (minStr.isNotEmpty()) minStr.toFloat() else 0f
            val maxBudget = if (maxStr.isNotEmpty()) maxStr.toFloat() else 0f

            // updating the shared preferences with the new financial targets
            pref.edit()
                .putFloat("BUDGET", income)
                .putFloat("MIN_BUDGET", minBudget)
                .putFloat("MAX_BUDGET", maxBudget)
                .apply()

            Toast.makeText(this, "Budget Goals Saved!", Toast.LENGTH_SHORT).show()
            
            // returning the user to the dashboard after saving successfully
            startActivity(Intent(this, ThirdActivity::class.java))
            finish()
        }

        // setting up the bottom navigation to allow switching between different screens
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_add 

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // navigating back to the home dashboard
                    startActivity(Intent(this, ThirdActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> true
                R.id.nav_progress -> {
                    // navigating to the user's financial progress screen
                    startActivity(Intent(this, ProgressActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_reports -> {
                    // navigating to the monthly reports screen
                    startActivity(Intent(this, ReportActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    // logging the user out and clearing the persistent session
    fun login_page(view: View) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

        val intent = Intent(this, SecondActivity::class.java)
        // clearing the activity stack so the user cannot navigate back after logging out
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
