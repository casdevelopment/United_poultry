package com.example.unitedpoultry.rider_expense.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.databinding.ItemExpenseReceiptBinding
import com.example.unitedpoultry.rider_expense.Model.ExpenseItem
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.Locale


class ExpenseReceiptAdapter(
    private val items: List<ExpenseItem>
) : RecyclerView.Adapter<ExpenseReceiptAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemExpenseReceiptBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpenseReceiptBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvExpenseTitle.text = item.title
        holder.binding.tvExpenseAmount.text = String.format(Locale.getDefault(), "Rs %d", item.amount)
    }

    override fun getItemCount(): Int = items.size
}