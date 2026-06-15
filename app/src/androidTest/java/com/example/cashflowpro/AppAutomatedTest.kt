package com.example.cashflowpro

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cashflowpro.data.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppAutomatedTest {
    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var budgetDao: BudgetDao
    private lateinit var badgeDao: BadgeDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        expenseDao = db.expenseDao()
        categoryDao = db.categoryDao()
        budgetDao = db.budgetDao()
        badgeDao = db.badgeDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun addExpense() = runBlocking {
        val expense = Expense(
            title = "Fuel",
            amount = 500.0,
            category = "Transport",
            date = "12/06/2026",
            notes = "Full tank"
        )
        expenseDao.insertExpense(expense)
        val allExpenses = expenseDao.getAllExpenses()
        assertEquals(1, allExpenses.size)
        assertEquals("Fuel", allExpenses[0].title)
        assertEquals(500.0, allExpenses[0].amount, 0.0)
    }

    @Test
    fun goalCalculation() = runBlocking {
        val budget = Budget(id = 1, min = 2000.0, max = 5000.0)
        budgetDao.insertBudget(budget)
        
        val expense = Expense(title = "Rent", amount = 3500.0, category = "Housing", date = "01/01/2024")
        expenseDao.insertExpense(expense)
        
        val totalSpent = expenseDao.getTotalExpenses() ?: 0.0
        val currentBudget = budgetDao.getBudget()!!
        
        assertTrue(totalSpent >= currentBudget.min && totalSpent <= currentBudget.max)
    }

    @Test
    fun addCategory() = runBlocking {
        val category = Category(name = "Education")
        categoryDao.insertCategory(category)
        
        val allCategories = categoryDao.getAllCategories()
        assertTrue(allCategories.any { it.name == "Education" })
    }

    @Test
    fun unlockBadge() = runBlocking {
        val badge = Badge(
            id = "beginner_saver",
            name = "Beginner Saver",
            description = "Logged your first expense!",
            icon = "🥉",
            isUnlocked = true,
            unlockDate = "20/10/2026"
        )
        badgeDao.insertBadge(badge)
        
        val unlockedBadge = badgeDao.getBadgeById("beginner_saver")
        assertTrue(unlockedBadge?.isUnlocked ?: false)
        assertEquals("20/10/2026", unlockedBadge?.unlockDate)
    }
}
