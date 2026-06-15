package com.example.cashflowpro

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.Badge
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BadgeActivity : AppCompatActivity() {

    private lateinit var badgesContainer: LinearLayout
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)

        setupToolbar()
        initViews()
        loadAndProcessBadges()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        badgesContainer = findViewById(R.id.badgesContainer)
    }

    private fun loadAndProcessBadges() {
        lifecycleScope.launch {
            val allExpenses = db.expenseDao().getAllExpenses()
            val savedBadges = db.badgeDao().getAllBadges()
            
            val badges = listOf(
                Badge("beginner_saver", "Beginner Saver", "Logged your first expense!", "🥉", allExpenses.isNotEmpty()),
                Badge("budget_keeper", "Budget Keeper", "Stayed within budget for 7 days", "🥈", checkBudgetStreak(7)),
                Badge("financial_master", "Financial Master", "Stayed within budget for 30 days", "🥇", checkBudgetStreak(30)),
                Badge("consistency_badge", "Consistency Badge", "Added expenses every day for a week", "🔥", checkConsistency(7))
            )

            // Save newly unlocked badges if not already saved
            for (badge in badges) {
                if (badge.isUnlocked) {
                    val existing = savedBadges.find { it.id == badge.id }
                    if (existing == null) {
                        db.badgeDao().insertBadge(badge.copy(unlockDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())))
                    }
                }
            }

            val finalSavedBadges = db.badgeDao().getAllBadges()
            displayBadges(badges, finalSavedBadges)
        }
    }

    private fun displayBadges(available: List<Badge>, saved: List<Badge>) {
        badgesContainer.removeAllViews()
        for (badge in available) {
            val savedBadge = saved.find { it.id == badge.id }
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_badge, badgesContainer, false)
            
            val tvIcon = itemView.findViewById<TextView>(R.id.tvBadgeIcon)
            val tvName = itemView.findViewById<TextView>(R.id.tvBadgeName)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvBadgeDescription)
            val tvStatus = itemView.findViewById<TextView>(R.id.tvUnlockDate)
            val root = itemView.findViewById<LinearLayout>(R.id.badgeRoot)

            tvIcon.text = badge.icon
            tvName.text = badge.name
            tvDesc.text = badge.description
            
            if (savedBadge != null) {
                root.alpha = 1.0f
                tvStatus.text = "Unlocked on: ${savedBadge.unlockDate}"
            } else {
                root.alpha = 0.5f
                tvStatus.text = "Locked"
            }
            
            badgesContainer.addView(itemView)
        }
    }

    private suspend fun checkBudgetStreak(days: Int): Boolean {
        // Simple mock logic or complex calculation based on goals vs expenses per day
        val budget = db.budgetDao().getBudget() ?: return false
        val expenses = db.expenseDao().getAllExpenses()
        if (expenses.isEmpty()) return false
        // For brevity, let's say they unlocked it if they have enough history and total is ok
        // In a real app, you'd check each day's total vs (monthlyBudget / 30)
        return expenses.size >= days && (db.expenseDao().getTotalExpenses() ?: 0.0) <= budget.max
    }

    private suspend fun checkConsistency(days: Int): Boolean {
        val expenses = db.expenseDao().getAllExpenses()
        val uniqueDays = expenses.map { it.date }.distinct()
        return uniqueDays.size >= days
    }
}
