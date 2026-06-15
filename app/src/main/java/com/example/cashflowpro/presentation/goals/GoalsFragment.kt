package com.example.cashflowpro.presentation.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.BudgetActivity
import com.example.cashflowpro.SavingsGoalActivity
import com.example.cashflowpro.R
import com.example.cashflowpro.data.AppDatabase
import com.example.cashflowpro.data.SavingsGoal
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch
import java.util.Locale

class GoalsFragment : Fragment() {

    private lateinit var tvBudgetRange: TextView
    private lateinit var rvSavingsGoals: RecyclerView
    
    private val db by lazy { AppDatabase.getDatabase(requireContext()) }
    private lateinit var adapter: SavingsGoalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goals, container, false)
    }

    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.btnSetBudget).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), BudgetActivity::class.java))
        }
        view.findViewById<View>(R.id.btnAddSavingsGoal).setOnClickListener {
            startActivity(android.content.Intent(requireContext(), SavingsGoalActivity::class.java))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecyclerView()
        setupListeners(view)
        loadGoals()
    }

    private fun initViews(view: View) {
        tvBudgetRange = view.findViewById(R.id.tvBudgetRange)
        rvSavingsGoals = view.findViewById(R.id.rvSavingsGoals)
    }

    private fun setupRecyclerView() {
        adapter = SavingsGoalAdapter(emptyList())
        rvSavingsGoals.layoutManager = LinearLayoutManager(requireContext())
        rvSavingsGoals.adapter = adapter
    }

    private fun loadGoals() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val budget = db.budgetDao().getBudget()
                if (budget != null) {
                    tvBudgetRange.text = String.format(Locale.getDefault(), "R%.0f - R%.0f", budget.min, budget.max)
                }

                val goals = db.savingsGoalDao().getAllGoals()
                adapter.updateData(goals)
            } catch (e: Exception) {
                android.util.Log.e("GoalsFragment", "Error loading goals", e)
            }
        }
    }

    inner class SavingsGoalAdapter(private var goals: List<SavingsGoal>) :
        RecyclerView.Adapter<SavingsGoalAdapter.GoalViewHolder>() {

        inner class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvGoalTitle)
            val tvProgress: TextView = view.findViewById(R.id.tvPercentage)
            val progressBar: LinearProgressIndicator = view.findViewById(R.id.goalProgress)
            val tvAmount: TextView = view.findViewById(R.id.tvAmountStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_savings_goal, parent, false)
            return GoalViewHolder(view)
        }

        override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
            val goal = goals[position]
            holder.tvName.text = goal.name
            val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount * 100).toInt().coerceIn(0, 100) else 0
            holder.tvProgress.text = String.format(Locale.getDefault(), "%d%%", progress)
            holder.progressBar.progress = progress
            holder.tvAmount.text = String.format(Locale.getDefault(), "R%.0f / R%.0f", goal.currentAmount, goal.targetAmount)
        }

        override fun getItemCount() = goals.size

        fun updateData(newGoals: List<SavingsGoal>) {
            goals = newGoals
            notifyDataSetChanged()
        }
    }
}
