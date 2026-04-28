package com.example.navigation

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// this class handles all the interactions with the local sqlite database
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Pennywise.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_EXPENSES = "expenses"

        // column names for the expense table
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date" // saved as YYYY-MM-DD for easier sorting
        private const val COLUMN_START_TIME = "start_time"
        private const val COLUMN_END_TIME = "end_time"
        private const val COLUMN_RECEIPT_URI = "receipt_uri"
    }

    // creating the database table when the app is first installed
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_EXPENSES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_START_TIME + " TEXT,"
                + COLUMN_END_TIME + " TEXT,"
                + COLUMN_RECEIPT_URI + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    // handling database updates if the version changes
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    // adding a new expense record to the database
    fun addExpense(amount: Float, category: String, description: String, date: String, startTime: String, endTime: String, receiptUri: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_AMOUNT, amount)
        values.put(COLUMN_CATEGORY, category)
        values.put(COLUMN_DESCRIPTION, description)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_START_TIME, startTime)
        values.put(COLUMN_END_TIME, endTime)
        values.put(COLUMN_RECEIPT_URI, receiptUri)

        val id = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return id
    }

    // fetching expenses from the database within a specific date range
    fun getExpensesByDate(startDate: String, endDate: String): Cursor {
        val db = this.readableDatabase
        // selecting all expenses where the date falls between the two provided dates
        return db.rawQuery("SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_DATE BETWEEN ? AND ? ORDER BY $COLUMN_DATE DESC", arrayOf(startDate, endDate))
    }

    // fetching all expenses ever recorded
    fun getAllExpenses(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_EXPENSES ORDER BY $COLUMN_DATE DESC", null)
    }

    // calculating the total amount spent across all recorded expenses
    fun getTotalExpenses(): Float {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES", null)
        var total = 0f
        if (cursor.moveToFirst()) {
            total = cursor.getFloat(0)
        }
        cursor.close()
        return total
    }
}
