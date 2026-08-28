package com.example.unitedpoultry.rider_expense.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.databinding.ItemProductBinding
import com.example.unitedpoultry.rider_expense.Model.ExpenseHead
import com.example.unitedpoultry.rider_expense.Model.ExpenseItemRequest

class ExpenseAdapter(
    private var expenseList: List<ExpenseHead>,
    private val onTotalChanged: (Double) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ProductViewHolder>() {

    private val amountMap = mutableMapOf<Int, Double>()

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        var textWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = expenseList[position]

        holder.binding.tvProductName.text = item.name

        // Remove old TextWatcher to avoid unwanted triggers during view recycling
        holder.textWatcher?.let { holder.binding.etQuantity.removeTextChangedListener(it) }

        val currentAmount = amountMap[item.id]
        if (currentAmount != null && currentAmount > 0) {
            val formatText = if (currentAmount % 1.0 == 0.0) currentAmount.toInt().toString() else currentAmount.toString()
            holder.binding.etQuantity.setText(formatText)
        } else {
            holder.binding.etQuantity.setText("")
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val amt = s.toString().toDoubleOrNull() ?: 0.0
                if (amt <= 0.0) {
                    amountMap.remove(item.id)
                } else {
                    amountMap[item.id] = amt
                }

                // Notify Activity to update UI total
                onTotalChanged(getTotalAmount())
            }
        }

        holder.binding.etQuantity.addTextChangedListener(watcher)
        holder.textWatcher = watcher
    }

    override fun getItemCount(): Int = expenseList.size

    fun updateData(newList: List<ExpenseHead>) {
        this.expenseList = newList
        notifyDataSetChanged()
    }

    // Collects only expenses where the entered amount is strictly greater than 0
    fun getEnteredExpenses(): List<ExpenseItemRequest> {
        return expenseList.mapNotNull { head ->
            val enteredAmount = amountMap[head.id] ?: 0.0
            if (enteredAmount > 0.0) {
                ExpenseItemRequest(
                    expense_id = head.id,
                    amount = enteredAmount
                )
            } else {
                null
            }
        }
    }

    fun getTotalAmount(): Double {
        return amountMap.values.sum()
    }
}