package com.example.cashflowpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflowpro.data.Expense

class HistoryAdapter(private var expenses: List<Expense>) :
    RecyclerView.Adapter<HistoryAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvCategoryIcon: TextView = view.findViewById(R.id.tvCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.tvDescription.text = expense.description
        holder.tvDate.text = expense.date
        holder.tvAmount.text = "R${String.format("%.2f", expense.amount)}"
        
        holder.tvCategoryIcon.text = when(expense.category.lowercase()) {
            "groceries" -> "🍞"
            "transport" -> "🚗"
            "entertainment" -> "🍿"
            "dining" -> "🍴"
            else -> "💰"
        }
    }

    override fun getItemCount() = expenses.size

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
