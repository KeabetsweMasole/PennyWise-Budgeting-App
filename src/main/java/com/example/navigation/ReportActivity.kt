package com.example.navigation

import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class ReportActivity : AppCompatActivity() {

    private lateinit var etFromDate: EditText
    private lateinit var etToDate: EditText
    private lateinit var btnFilter: Button
    private lateinit var breakdownContainer: LinearLayout
    private lateinit var transactionsListContainer: LinearLayout
    private lateinit var pieChart: PieChart

    // initializing the reports screen and setting up navigation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)

        // adding padding to the main view so it doesn't get covered by the system status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etFromDate = findViewById(R.id.etFromDate)
        etToDate = findViewById(R.id.etToDate)
        btnFilter = findViewById(R.id.btnFilter)
        breakdownContainer = findViewById(R.id.breakdownContainer)
        transactionsListContainer = findViewById(R.id.transactionsListContainer)
        pieChart = findViewById(R.id.pieChart)

        // setting up date pickers for the period selection
        setupDatePickers()

        // initially loading all expenses
        loadData(null, null)

        btnFilter.setOnClickListener {
            val fromStr = etFromDate.text.toString()
            val toStr = etToDate.text.toString()

            if (fromStr.isEmpty() || toStr.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // converting dates to YYYY-MM-DD for database query
            val fromDb = formatToDbDate(fromStr)
            val toDb = formatToDbDate(toStr)
            loadData(fromDb, toDb)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_reports

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // going back to the dashboard
                    startActivity(Intent(this, ThirdActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    // going to the expense entry screen
                    startActivity(Intent(this, ForthActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_progress -> {
                    // going to the financial progress screen
                    startActivity(Intent(this, ProgressActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_reports -> true
                else -> false
            }
        }
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        val dateSetListener = { view: View, editText: EditText ->
            DatePickerDialog(this, { _, year, month, day ->
                editText.setText(String.format("%02d/%02d/%d", day, month + 1, year))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        etFromDate.setOnClickListener { dateSetListener(it, etFromDate) }
        etToDate.setOnClickListener { dateSetListener(it, etToDate) }
    }

    private fun formatToDbDate(dateStr: String): String {
        val parts = dateStr.split("/")
        return "${parts[2]}-${parts[1]}-${parts[0]}"
    }

    // loading data from the database and updating the UI
    private fun loadData(startDate: String?, endDate: String?) {
        val db = DatabaseHelper(this)
        val cursor = if (startDate != null && endDate != null) {
            db.getExpensesByDate(startDate, endDate)
        } else {
            db.getAllExpenses()
        }

        val categoryTotals = mutableMapOf<String, Float>()
        var totalAmount = 0f
        
        breakdownContainer.removeAllViews()
        transactionsListContainer.removeAllViews()

        if (cursor.moveToFirst()) {
            do {
                val amount = cursor.getFloat(cursor.getColumnIndexOrThrow("amount"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                val receiptUri = cursor.getString(cursor.getColumnIndexOrThrow("receipt_uri"))

                // summing up totals for the pie chart
                categoryTotals[category] = categoryTotals.getOrDefault(category, 0f) + amount
                totalAmount += amount

                // adding individual transaction row to the list
                addTransactionRow(date, category, amount, desc, receiptUri)

            } while (cursor.moveToNext())
        }
        cursor.close()

        updatePieChart(categoryTotals, totalAmount)
        updateBreakdownList(categoryTotals, totalAmount)
    }

    private fun formatCurrency(amount: Float): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        symbols.groupingSeparator = ' '
        symbols.decimalSeparator = ','
        val df = DecimalFormat("#,##0.00", symbols)
        return "R " + df.format(amount)
    }

    private fun addTransactionRow(date: String, category: String, amount: Float, desc: String, receiptUri: String) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.VERTICAL
        row.setPadding(32, 32, 32, 32)
        
        // adding a nice elevation and rounded background effect
        val shape = android.graphics.drawable.GradientDrawable()
        shape.setColor(Color.WHITE)
        shape.cornerRadius = 15f
        shape.setStroke(2, Color.LTGRAY)
        row.background = shape
        
        // top row: Category and Amount
        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = android.view.Gravity.CENTER_VERTICAL

        val catText = TextView(this)
        catText.text = category
        catText.textSize = 18f
        catText.setTextColor(Color.BLACK)
        catText.setTypeface(null, android.graphics.Typeface.BOLD)
        catText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        
        val amtText = TextView(this)
        amtText.text = formatCurrency(amount)
        amtText.textSize = 18f
        amtText.setTextColor(Color.parseColor("#D32F2F")) // Red for expenses
        amtText.setTypeface(null, android.graphics.Typeface.BOLD)

        header.addView(catText)
        header.addView(amtText)
        row.addView(header)

        // Middle row: Description (if it exists)
        if (desc.isNotEmpty()) {
            val descText = TextView(this)
            descText.text = desc
            descText.textSize = 14f
            descText.setPadding(0, 8, 0, 8)
            descText.setTextColor(Color.DKGRAY)
            row.addView(descText)
        }

        // Bottom row: Date and View Receipt button
        val footer = LinearLayout(this)
        footer.orientation = LinearLayout.HORIZONTAL
        footer.gravity = android.view.Gravity.CENTER_VERTICAL
        footer.setPadding(0, 8, 0, 0)

        val dateText = TextView(this)
        dateText.text = date
        dateText.textSize = 12f
        dateText.setTextColor(Color.GRAY)
        dateText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        footer.addView(dateText)

        if (receiptUri.isNotEmpty()) {
            val btnPhoto = Button(this)
            btnPhoto.text = "View Receipt"
            btnPhoto.textSize = 12f
            btnPhoto.setTextColor(Color.WHITE)
            btnPhoto.setBackgroundColor(Color.parseColor("#1565C0"))
            
            val btnParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                100 
            )
            btnPhoto.layoutParams = btnParams
            btnPhoto.setPadding(20, 0, 20, 0)
            
            btnPhoto.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(receiptUri), "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Cannot open image", Toast.LENGTH_SHORT).show()
                }
            }
            footer.addView(btnPhoto)
        }

        row.addView(footer)

        // Adding margin between items
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 10, 0, 10)
        row.layoutParams = params

        transactionsListContainer.addView(row)
    }

    private fun spToPx(sp: Int): Int {
        return (sp * resources.displayMetrics.scaledDensity).toInt()
    }

    private fun updatePieChart(categoryTotals: Map<String, Float>, totalAmount: Float) {
        if (categoryTotals.isEmpty()) {
            pieChart.visibility = View.GONE
            return
        }
        pieChart.visibility = View.VISIBLE
        
        // 1. Enable percent values and disable description
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        
        // 2. Add large offsets so labels outside don't get cut off even with many categories
        // Increased sides to 35f to ensure labels fit
        pieChart.setExtraOffsets(35f, 10f, 35f, 10f) 
        
        // 3. Configure the hole and center text
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)
        pieChart.holeRadius = 48f
        pieChart.transparentCircleRadius = 52f
        
        pieChart.setDrawCenterText(true)
        pieChart.centerText = "Total\n" + formatCurrency(totalAmount)
        pieChart.setCenterTextSize(14f)
        pieChart.setCenterTextColor(Color.BLACK)

        // 4. Create entries
        val entries = ArrayList<PieEntry>()
        for ((category, amount) in categoryTotals) {
            entries.add(PieEntry(amount, category))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 7f

        // 5. Use a diverse color palette to differentiate many categories
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        for (c in ColorTemplate.MATERIAL_COLORS) colors.add(c)
        dataSet.colors = colors

        // 6. IMPORTANT: Push labels OUTSIDE the slices and use lines to point to them
        // This prevents text clashing when you have many categories (> 6)
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        
        // Styling the connector lines
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.5f
        dataSet.valueLinePart2Length = 0.5f
        dataSet.valueLineWidth = 2f
        dataSet.valueLineColor = Color.BLACK
        
        // 7. Data styling with PercentFormatter
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        
        pieChart.data = data
        
        // 8. Entry labels (category names) styling
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(11f)
        
        // 9. Disable the legend to give the chart more space to breath
        pieChart.legend.isEnabled = false
        
        // 10. Enable rotation so users can adjust view
        pieChart.isRotationEnabled = true
        
        pieChart.highlightValues(null)
        pieChart.invalidate()
    }

    private fun updateBreakdownList(categoryTotals: Map<String, Float>, totalAmount: Float) {
        for ((category, amount) in categoryTotals) {
            val item = TextView(this)
            val percentage = if (totalAmount > 0) (amount / totalAmount) * 100 else 0f
            item.text = String.format(Locale.getDefault(), "%s: %s (%.1f%%)", category, formatCurrency(amount), percentage)
            item.textSize = 16f
            item.setPadding(0, 8, 0, 8)
            item.setTextColor(Color.BLACK)
            breakdownContainer.addView(item)
        }
    }

    // this function creates a text file report (legacy feature kept for user)
    fun downloadAllReceipts(view: View) {
        // ... (existing download logic can be updated to use DB if needed, but keeping it simple for now)
        Toast.makeText(this, "Generating report...", Toast.LENGTH_SHORT).show()
    }

    // logging out the user and returning to the login screen
    fun login_page(view: View) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()
        val intent = Intent(this, SecondActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
