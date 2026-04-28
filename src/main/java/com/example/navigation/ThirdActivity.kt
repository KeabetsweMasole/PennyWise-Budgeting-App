package com.example.navigation

import android.graphics.Color
import android.content.Intent
import android.net.Uri
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class ThirdActivity : AppCompatActivity() {

    private lateinit var tvPocket: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvExpenses: TextView
    private lateinit var tvMinBudget: TextView
    private lateinit var tvMaxBudget: TextView
    private lateinit var tvUsername: TextView
    private lateinit var transactionsContainer: LinearLayout

    // setting up the screen and all the text views for the dashboard
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third)

        // adding padding to the main view so it doesn't get covered by the system status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvPocket = findViewById(R.id.tvPocket)
        tvIncome = findViewById(R.id.tvIncome)
        tvExpenses = findViewById(R.id.tvExpenses)
        tvMinBudget = findViewById(R.id.tvMinBudget)
        tvMaxBudget = findViewById(R.id.tvMaxBudget)
        tvUsername = findViewById(R.id.tv_username)
        transactionsContainer = findViewById(R.id.transactionsContainer)

        // this part handles the bottom navigation bar to move between different activities
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_add -> {
                    // going to the budget settings screen
                    startActivity(Intent(this, BudgetActivity::class.java))
                    true
                }
                R.id.nav_progress -> {
                    // going to the user progress screen
                    startActivity(Intent(this, ProgressActivity::class.java))
                    true
                }
                R.id.nav_reports -> {
                    // going to the reports and charts screen
                    startActivity(Intent(this, ReportActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // this runs whenever the user returns to the dashboard screen
    override fun onResume() {
        super.onResume()
        // calling the function to update the screen with latest data
        refreshData()
    }

    // this helper function formats numbers with spaces for thousands and a comma for decimals
    private fun formatCurrency(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        symbols.groupingSeparator = ' '
        symbols.decimalSeparator = ','
        val df = DecimalFormat("#,##0.00", symbols)
        return "R " + df.format(amount)
    }

    // updating all the financial totals and the welcome message
    private fun refreshData() {
        val userPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val name = userPref.getString("REG_USER", "Guest")
        tvUsername.text = String.format("Welcome back, %s", name)

        val db = DatabaseHelper(this)
        val totalExpenses = db.getTotalExpenses().toDouble()

        val sharedPref = getSharedPreferences("FinancePrefs", MODE_PRIVATE)
        val income = sharedPref.getFloat("BUDGET", 0f).toDouble()
        val minBudget = sharedPref.getFloat("MIN_BUDGET", 0f).toDouble()
        val maxBudget = sharedPref.getFloat("MAX_BUDGET", 0f).toDouble()
        val remaining = income - totalExpenses

        // displaying the formatted currency values on the dashboard
        tvPocket.text = formatCurrency(remaining)
        tvIncome.text = formatCurrency(income)
        tvExpenses.text = formatCurrency(totalExpenses)
        tvMinBudget.text = formatCurrency(minBudget)
        tvMaxBudget.text = formatCurrency(maxBudget)

        // loading the list of recent transactions from the database
        loadTransactions()
    }

    // generating the list of transactions and adding them to the layout
    private fun loadTransactions() {
        val db = DatabaseHelper(this)
        val cursor = db.getAllExpenses()

        // clearing the container before adding new items
        transactionsContainer.removeAllViews()

        if (!cursor.moveToFirst()) {
            // showing a message if there are no transactions yet
            val emptyText = TextView(this)
            emptyText.text = "No transactions yet"
            emptyText.setPadding(16, 16, 16, 16)
            transactionsContainer.addView(emptyText)
            cursor.close()
            return
        }

        // looping through all transactions from the cursor
        do {
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
            val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val receiptUri = cursor.getString(cursor.getColumnIndexOrThrow("receipt_uri"))

            // creating a horizontal layout for each transaction row
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(16, 16, 16, 16)

            val left = LinearLayout(this)
            left.orientation = LinearLayout.VERTICAL
            left.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            // setting up the transaction category and description text
            val title = TextView(this)
            title.text = category
            title.textSize = 16f
            title.setTextColor(resources.getColor(android.R.color.black, null))
            
            val desc = TextView(this)
            desc.text = description
            desc.textSize = 12f

            left.addView(title)
            left.addView(desc)

            // formatting and styling the expense amount
            val amountView = TextView(this)
            amountView.text = "- " + formatCurrency(amount)
            amountView.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            amountView.setTypeface(null, Typeface.BOLD)

            // setting up the receipt button with a small compact design
            val btnAction = Button(this)
            btnAction.text = "Receipt"
            btnAction.textSize = 8f
            btnAction.setTextColor(Color.WHITE)
            btnAction.setBackgroundColor(Color.parseColor("#0D47A1"))
            btnAction.minHeight = 0
            btnAction.minimumHeight = 0
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                70 
            )
            params.setMargins(10, 0, 0, 0)
            btnAction.layoutParams = params
            btnAction.setPadding(10, 0, 10, 0)
            
            // showing options when the receipt button is clicked
            btnAction.setOnClickListener {
                showReceiptOptions(category, amount, description, receiptUri)
            }

            row.addView(left)
            row.addView(amountView)
            row.addView(btnAction)

            // adding the row to the main transactions list on the dashboard
            // Note: Since getAllExpenses usually returns oldest first, we add to top if we want newest first
            // Or change query in DatabaseHelper. Here I'll add them as they come.
            transactionsContainer.addView(row, 0) 
        } while (cursor.moveToNext())
        
        cursor.close()
    }

    // creating the popup to either view the receipt details or save it as a file
    private fun showReceiptOptions(category: String, amount: Double, desc: String, receiptUri: String) {
        val receiptContent = """
            TRANSACTION RECEIPT
            -----------------------------
            Category: $category
            Amount: ${formatCurrency(amount)}
            Description: $desc
            Date: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(java.util.Date())}
            -----------------------------
            Thank you for using Pennywise!
        """.trimIndent()

        val hasImage = receiptUri.isNotEmpty()
        val options = if (hasImage) {
            arrayOf("View Details", "View Photo Receipt", "Download Text Receipt")
        } else {
            arrayOf("View Details", "Download Text Receipt")
        }

        AlertDialog.Builder(this)
            .setTitle("Transaction Options")
            .setItems(options) { _, which ->
                when {
                    options[which] == "View Details" -> {
                        AlertDialog.Builder(this)
                            .setTitle("Receipt Details")
                            .setMessage(receiptContent)
                            .setPositiveButton("Close", null)
                            .show()
                    }
                    options[which] == "View Photo Receipt" -> {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(Uri.parse(receiptUri), "image/*")
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(this, "Cannot open image", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        saveToFile("Receipt_${System.currentTimeMillis()}.txt", receiptContent)
                    }
                }
            }
            .show()
    }

    // this function handles saving a text file to the app's external storage folder
    private fun saveToFile(fileName: String, content: String) {
        try {
            val file = File(getExternalFilesDir(null), fileName)
            val fos = FileOutputStream(file)
            fos.write(content.toByteArray())
            fos.close()
            // notifying the user that the file has been saved successfully
            Toast.makeText(this, "Receipt saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            // showing an error if the file could not be saved
            Toast.makeText(this, "Failed to save receipt", Toast.LENGTH_SHORT).show()
        }
    }

    // navigating to the add expense activity
    fun Expenses_page(view: View) {
        val intent = Intent(this, ForthActivity::class.java)
        // removing animation for a faster screen transition
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    // logging the user out and clearing the persistent login state
    fun login_page(view: View) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

        val intent = Intent(this, SecondActivity::class.java)
        // clearing the activity stack so the user cannot go back to the dashboard
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
