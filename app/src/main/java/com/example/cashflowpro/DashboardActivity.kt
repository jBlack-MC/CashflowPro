package com.example.cashflowpro

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setupWithNavController(navController)

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            showQuickActions()
        }
    }

    private fun showQuickActions() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_quick_actions, null)
        
        view.findViewById<View>(R.id.actionAddExpense).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
            dialog.dismiss()
        }
        
        view.findViewById<View>(R.id.actionAddIncome).setOnClickListener {
            startActivity(Intent(this, AddIncomeActivity::class.java))
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.actionAddGoal).setOnClickListener {
            startActivity(Intent(this, SavingsGoalActivity::class.java))
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.actionAddCategory).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}
