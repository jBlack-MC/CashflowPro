package com.example.cashflowpro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.data.AppDatabase
import kotlinx.coroutines.launch

class BadgeActivity : AppCompatActivity() {

    private lateinit var badgesContainer: LinearLayout
    private lateinit var tvBadgeCount: TextView
    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)

        setupToolbar()
        initViews()
        loadBadges()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        badgesContainer = findViewById(R.id.badgesContainer)
        tvBadgeCount = findViewById(R.id.tvBadgeCount)
    }

    private fun loadBadges() {
        lifecycleScope.launch {
            val expenses = db.expenseDao().getAllExpenses()
            val goals = db.savingsGoalDao().getAllGoals()
            val income = db.incomeDao().getAllIncome()

            val allBadges = listOf(
                BadgeInfo("First Expense", "Log your first expense", "🥉", expenses.isNotEmpty()),
                BadgeInfo("Smart Saver", "Reach 50% of a savings goal", "🥈", goals.any { it.targetAmount > 0 && it.currentAmount / it.targetAmount >= 0.5 }),
                BadgeInfo("High Earner", "Log income over R10,000", "💰", income.any { it.amount >= 10000 }),
                BadgeInfo("Goal Getter", "Complete one savings goal", "🎯", goals.any { it.targetAmount > 0 && it.currentAmount >= it.targetAmount }),
                BadgeInfo("Massive Saver", "Save more than R5,000 in total", "💎", goals.sumOf { it.currentAmount } >= 5000),
                BadgeInfo("Data Keeper", "Log more than 10 transactions", "📊", expenses.size >= 10)
            )

            val unlockedCount = allBadges.count { it.isUnlocked }
            tvBadgeCount.text = "$unlockedCount / ${allBadges.size}"

            badgesContainer.removeAllViews()
            val inflater = LayoutInflater.from(this@BadgeActivity)

            for (badge in allBadges) {
                val badgeView = inflater.inflate(R.layout.item_badge, badgesContainer, false)
                
                badgeView.findViewById<TextView>(R.id.tvBadgeName).text = badge.name
                badgeView.findViewById<TextView>(R.id.tvBadgeDescription).text = badge.description
                badgeView.findViewById<TextView>(R.id.tvBadgeIcon).text = badge.icon
                
                val statusText = badgeView.findViewById<TextView>(R.id.tvUnlockDate)
                val root = badgeView.findViewById<View>(R.id.badgeRoot)

                if (badge.isUnlocked) {
                    statusText.text = "Unlocked"
                    root.alpha = 1.0f
                } else {
                    statusText.text = "Locked"
                    statusText.setTextColor(getColor(R.color.text_hint))
                    root.alpha = 0.5f
                }

                badgesContainer.addView(badgeView)
            }
        }
    }

    data class BadgeInfo(
        val name: String,
        val description: String,
        val icon: String,
        val isUnlocked: Boolean
    )
}
