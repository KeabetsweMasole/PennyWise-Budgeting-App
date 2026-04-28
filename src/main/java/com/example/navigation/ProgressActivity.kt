package com.example.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProgressActivity : AppCompatActivity() {

    // initializing the progress activity and setting up the level and streak UI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_progress)

        // adding padding for the system bars to prevent UI overlapping
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // connecting the layout components to the code
        val tvLevel = findViewById<TextView>(R.id.tvLevel)
        val progressXP = findViewById<ProgressBar>(R.id.progressXP)
        val tvStreak = findViewById<TextView>(R.id.tvStreak)
        val badgeContainer = findViewById<LinearLayout>(R.id.badgeContainer)

        // loading the user's progress data from storage
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val xp = prefs.getInt("xp", 0)
        // updating the daily login streak
        val streak = updateStreak() 

        // calculating the user's level based on total XP (100 XP per level)
        val level = (xp / 100) + 1
        val progress = xp % 100

        // updating the text and progress bar on the screen
        tvLevel.text = "Level $level"
        progressXP.progress = progress
        tvStreak.text = "$streak Days"

        // showing the badges the user has earned so far
        loadBadges(badgeContainer, xp, streak)

        // setting up the bottom navigation menu
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_progress

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // going back to the dashboard
                    startActivity(Intent(this, ThirdActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    // going to the budget settings screen
                    startActivity(Intent(this, BudgetActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_progress -> true 
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

    // this function updates the daily login streak by checking the time since last login
    private fun updateStreak(): Int {
        val prefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = prefs.edit()

        val lastLogin = prefs.getLong("lastLogin", 0)
        val currentTime = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L

        var streak = prefs.getInt("streak", 0)

        if (lastLogin != 0L) {
            // checking if the user logged in yesterday to increase the streak
            if (currentTime - lastLogin in 1..oneDay) {
                streak++ 
            } else if (currentTime - lastLogin > oneDay) {
                // resetting the streak if the user missed a day
                streak = 1 
            }
        } else {
            // first time logging in
            streak = 1 
        }

        // saving the updated streak and the current login time
        editor.putInt("streak", streak)
        editor.putLong("lastLogin", currentTime)
        editor.apply()

        return streak
    }

    // this function displays achievement badges based on user performance
    private fun loadBadges(container: LinearLayout, xp: Int, streak: Int) {
        // clearing the badge list before adding new ones
        container.removeAllViews()

        // adding the first expense badge if the user has at least 10 XP
        if (xp >= 10) addBadge(container)

        // adding the level 2 badge if the user has reached 100 XP
        if (xp >= 100) addBadge(container)

        // adding the loyalty badge for a 3-day login streak
        if (streak >= 3) addBadge(container)

        // adding a master badge for reaching 300 XP
        if (xp >= 300) addBadge(container)
    }

    // helper function to create and add a badge icon to the screen
    private fun addBadge(container: LinearLayout) {
        val badge = ImageView(this)
        // using a simple star icon for the badge
        badge.setImageResource(android.R.drawable.star_big_on)

        val params = LinearLayout.LayoutParams(120, 120)
        params.setMargins(10, 10, 10, 10)

        badge.layoutParams = params
        container.addView(badge)
    }

    // logging the user out and returning to the login page
    fun login_page(view: View) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

        val intent = Intent(this, SecondActivity::class.java)
        // clearing the activity history for a secure logout
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
