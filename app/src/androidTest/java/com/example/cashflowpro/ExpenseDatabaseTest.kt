package com.example.cashflowpro

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Expense
import com.example.cashflowpro.data.ExpenseDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ExpenseDatabaseTest {
    private lateinit var expenseDao: ExpenseDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        expenseDao = db.expenseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeExpenseAndReadInList() = runBlocking {
        val expense = Expense(
            amount = 150.0,
            category = "Groceries",
            description = "Weekly shop",
            date = "2024-03-20",
            startTime = "10:00",
            endTime = "10:30",
            imageUri = null
        )
        expenseDao.insertExpense(expense)
        val allExpenses = expenseDao.getAllExpenses()
        assertEquals(allExpenses[0].amount, 150.0, 0.0)
    }

    @Test
    @Throws(Exception::class)
    fun calculateTotalExpenses() = runBlocking {
        val expense1 = Expense(
            amount = 100.0,
            category = "Food",
            description = "Lunch",
            date = "2024-03-20",
            startTime = "12:00",
            endTime = "12:30",
            imageUri = null
        )
        val expense2 = Expense(
            amount = 200.0,
            category = "Fuel",
            description = "Petrol",
            date = "2024-03-20",
            startTime = "14:00",
            endTime = "14:15",
            imageUri = null
        )
        expenseDao.insertExpense(expense1)
        expenseDao.insertExpense(expense2)
        
        val total = expenseDao.getTotalExpenses()
        assertEquals(300.0, total, 0.0)
    }
}
