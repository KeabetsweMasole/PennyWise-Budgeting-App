package com.example.navigation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ForthActivity : AppCompatActivity() {

    private var receiptUri: Uri? = null
    private lateinit var imgReceipt: ImageView

    // handling the image picker result for uploading a receipt
    private val pickImage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            receiptUri = it
            // take persistable permission to ensure we can access this image later
            try {
                val contentResolver = applicationContext.contentResolver
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // displaying the selected receipt image on the screen
            imgReceipt.setImageURI(it)
        }
    }

    // initializing the activity and setting up all the input fields and buttons
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forth)

        // adding padding to the main view so it doesn't get covered by the system status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // finding all the views from the layout file
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val spinner = findViewById<Spinner>(R.id.spCategory)
        val etCustomCategory = findViewById<EditText>(R.id.etCustomCategory)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etStartTime = findViewById<EditText>(R.id.etStartTime)
        val etEndTime = findViewById<EditText>(R.id.etEndTime)
        val btnUpload = findViewById<Button>(R.id.btnUpload)
        imgReceipt = findViewById(R.id.imgReceipt)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // setting up the dropdown list for expense categories
        val categories = mutableListOf("Food", "Transport", "Bills", "Shopping", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = adapter

        // showing the custom category input field only when 'Other' is selected
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (categories[position] == "Other") {
                    etCustomCategory.visibility = View.VISIBLE
                } else {
                    etCustomCategory.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // opening a date picker dialog when the date field is clicked
        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                // formatting and setting the selected date
                etDate.setText(String.format("%02d/%02d/%d", day, month + 1, year))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // helper function to show a time picker dialog
        fun showTimePicker(editText: EditText) {
            val c = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                // formatting and setting the selected time
                editText.setText(String.format("%02d:%02d", hour, minute))
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        // triggering the time picker for start and end times
        etStartTime.setOnClickListener { showTimePicker(etStartTime) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime) }

        // launching the image gallery to pick a receipt photo
        btnUpload.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }

        // handling the save button click to store the expense data
        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            // making sure the user has entered an amount
            if (amountText.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toFloat()

            // check if user has enough "in my pocket" money
            val financePref = getSharedPreferences("FinancePrefs", MODE_PRIVATE)
            val budget = financePref.getFloat("BUDGET", 0f)
            val db = DatabaseHelper(this)
            val currentTotalExpenses = db.getTotalExpenses()
            val remaining = budget - currentTotalExpenses

            if (amount > remaining) {
                Toast.makeText(this, "Insufficient funds! You only have ${String.format("%.2f", remaining)} left.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var category = spinner.selectedItem.toString()
            // using the custom category if the user chose 'Other'
            if (category == "Other") {
                category = etCustomCategory.text.toString()
                if (category.isEmpty()) category = "Other"
            }

            val dateStr = etDate.text.toString()
            // converting DD/MM/YYYY to YYYY-MM-DD for database sorting
            val dbDate = if (dateStr.isNotEmpty()) {
                val parts = dateStr.split("/")
                if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else dateStr
            } else {
                java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            }

            // saving the new transaction into the local sqlite database
            db.addExpense(
                amount,
                category,
                etDescription.text.toString(),
                dbDate,
                etStartTime.text.toString(),
                etEndTime.text.toString(),
                receiptUri?.toString() ?: ""
            )

            // updating the total expenses in shared prefs for quick dashboard access
            val pref = getSharedPreferences("FinancePrefs", MODE_PRIVATE)
            pref.edit()
                .putFloat("TOTAL_EXPENSE", pref.getFloat("TOTAL_EXPENSE", 0f) + amount)
                .apply()

            // awarding the user 10 xp points for adding a new expense
            val userPref = getSharedPreferences("UserData", MODE_PRIVATE)
            userPref.edit()
                .putInt("xp", userPref.getInt("xp", 0) + 10)
                .apply()

            Toast.makeText(this, "Expense Saved! +10 XP", Toast.LENGTH_SHORT).show()
            // closing the activity and going back
            finish()
        }

        // setting up the bottom navigation menu
        val nav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        nav.selectedItemId = R.id.nav_add

        nav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // going back to the home screen
                    startActivity(Intent(this, ThirdActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> true
                R.id.nav_progress -> {
                    // going to the progress tracking screen
                    startActivity(Intent(this, ProgressActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_reports -> {
                    // going to the expense reports screen
                    startActivity(Intent(this, ReportActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    // logging out the user and clearing session data
    fun login_page(view: View) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

        val intent = Intent(this, SecondActivity::class.java)
        // clearing the navigation stack for a clean logout
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
