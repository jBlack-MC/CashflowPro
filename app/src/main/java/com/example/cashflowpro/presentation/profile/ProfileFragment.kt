package com.example.cashflowpro.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.cashflowpro.*
import com.example.cashflowpro.data.AppDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var tvXpStatus: TextView
    private lateinit var xpProgressBar: LinearProgressIndicator
    private lateinit var tvLevelName: TextView
    private val db by lazy { AppDatabase.getDatabase(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tvXpStatus = view.findViewById(R.id.tvXpStatus)
        xpProgressBar = view.findViewById(R.id.xpProgressBar)
        tvLevelName = view.findViewById(R.id.tvLevelName)

        setupListeners(view)
        loadUserProgress()
    }

    private fun setupListeners(view: View) {
        view.findViewById<MaterialButton>(R.id.btnReports).setOnClickListener {
            startActivity(Intent(requireContext(), ReportsActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.btnAchievements).setOnClickListener {
            startActivity(Intent(requireContext(), BadgeActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.btnManageCategories).setOnClickListener {
            startActivity(Intent(requireContext(), CategoryActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadUserProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            val expenseCount = db.expenseDao().getAllExpenses().size
            val goalCount = db.savingsGoalDao().getAllGoals().size
            
            // Logic: 50 XP per expense, 200 XP per goal
            val totalXp = (expenseCount * 50) + (goalCount * 200)
            val level = (totalXp / 1000) + 1
            val progress = totalXp % 1000

            tvXpStatus.text = "$progress / 1000 XP (Level $level)"
            xpProgressBar.progress = progress / 10
            
            tvLevelName.text = when {
                level < 2 -> "Budget Rookie"
                level < 5 -> "Thrifty Saver"
                level < 10 -> "Finance Whiz"
                else -> "Wealth Master"
            }
        }
    }
}
