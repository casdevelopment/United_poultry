package com.example.unitedpoultry.History.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.History.CollectionHistoryActivity
import com.example.unitedpoultry.History.SaleHistoryActivity
import com.example.unitedpoultry.History.model.HistoryModel
import com.example.unitedpoultry.R

class HistoryAdapter(
    private val list: MutableList<HistoryModel>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val originalList = ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_shop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvShopName.text = item.name
        holder.tvDescription.text = item.description
        holder.tvAmount.text = "Rs. ${item.amount}"
        holder.tvTime.text = item.time

        if (item.type.equals("Sale", true)) {

            // Sale → Box icon + black amount
            holder.ivIcon.setImageResource(R.drawable.boxesvector)
            holder.tvAmount.setTextColor(
                holder.itemView.context.getColor(R.color.black)
            )

        } else if (item.type.equals("Collection", true)) {

            // Collection → Card icon + green amount
            holder.ivIcon.setImageResource(R.drawable.activitycardvector)
            holder.tvAmount.setTextColor(
                holder.itemView.context.getColor(R.color.green)
            )
        }


        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = if (item.type.equals("Sale", true)) {
                Intent(context, SaleHistoryActivity::class.java)
            } else {
                Intent(context, CollectionHistoryActivity::class.java)
            }

            // Pass data
            intent.putExtra("name", item.name)
            intent.putExtra("amount", item.amount)

            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = list.size

    // 🔥 COMBINED FILTER (Top + Bottom)
    fun applyFilter(type: String, time: String) {
        list.clear()

        list.addAll(originalList.filter { item ->
            val typeMatch =
                type == "All" || item.type.equals(type, true)

            val timeMatch = when (time) {
                "Today" -> item.day == 0
                "Week" -> item.day <= 7
                "Month" -> item.day <= 30
                else -> true
            }

            typeMatch && timeMatch
        })

        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvShopName: TextView = v.findViewById(R.id.tvShopName)
        val tvDescription: TextView = v.findViewById(R.id.tvDescription)
        val tvAmount: TextView = v.findViewById(R.id.tvAmount)
        val tvTime: TextView = v.findViewById(R.id.tvTime)
        val ivIcon: ImageView = v.findViewById(R.id.ivIcon)
    }
}
