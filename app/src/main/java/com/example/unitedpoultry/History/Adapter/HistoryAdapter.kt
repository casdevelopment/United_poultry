package com.example.unitedpoultry.History.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.History.CollectionHistoryActivity
import com.example.unitedpoultry.History.ExpenseHistoryActivity
import com.example.unitedpoultry.History.SaleHistoryActivity
import com.example.unitedpoultry.History.model.TransactionItem
import com.example.unitedpoultry.R
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val transactions: List<TransactionItem>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvShopName: TextView = itemView.findViewById(R.id.tvShopName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
      //  val tvPaymentType: TextView = itemView.findViewById(R.id.tvPaymentType)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_shop, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        val item = transactions[position]

        if (item.transaction_type.equals("expense", ignoreCase = true)) {

            holder.tvShopName.text = item.description
            holder.tvDescription.text = "${item.transaction_type}, ${item.payment_type}"
            holder.tvAmount.text = "Rs ${item.amount}"
            holder.tvDate.text = formatDate(item.transaction_at)

            holder.ivIcon.setImageResource(R.drawable.expenseiconsvg)

            holder.tvAmount.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.primary)
            )

            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, ExpenseHistoryActivity::class.java)
                intent.putExtra("ID", item.id)
                context.startActivity(intent)
            }

        } else {

            holder.tvShopName.text = item.shop_name
            holder.tvDescription.text = item.description
            holder.tvAmount.text = "Rs ${item.amount}"
            holder.tvDate.text = formatDate(item.transaction_at)


            if (item.transaction_type.equals("sale", ignoreCase = true)) {
                holder.ivIcon.setImageResource(R.drawable.boxprimarysvg) // your sale icon

                holder.tvAmount.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.black)
                )

                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, SaleHistoryActivity::class.java)
                    intent.putExtra("ID", item.id)
                    context.startActivity(intent)
                }

            } else if (item.transaction_type.equals("collection", ignoreCase = true)) {
                holder.ivIcon.setImageResource(R.drawable.collectionhistroysvg) // your collection icon
                holder.tvAmount.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.primary)
                )

                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, CollectionHistoryActivity::class.java)
                    intent.putExtra("ID", item.id)
                    context.startActivity(intent)
                }
            } else {
                // Default / unknown type
                holder.ivIcon.setImageResource(R.drawable.collectionhistroysvg)
                holder.tvAmount.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.black)
                )
            }


        }

    }

    override fun getItemCount(): Int = transactions.size

    // Format date: "2026-03-02 07:28:34" → "2 Mar 2026, 07:28 AM"
    private fun formatDate(dateStr: String?): String {
        if (dateStr.isNullOrEmpty()) return ""
        return try {
            val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = serverFormat.parse(dateStr)
         //   val displayFormat = SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.getDefault())

            val displayFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            displayFormat.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }
}