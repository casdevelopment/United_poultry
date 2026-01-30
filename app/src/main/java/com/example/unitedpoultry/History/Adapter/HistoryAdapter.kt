package com.example.unitedpoultry.History.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.History.SaleHistoryActivity
import com.example.unitedpoultry.History.model.SaleItem
import com.example.unitedpoultry.R

class HistoryAdapter(
    private val list: MutableList<SaleItem>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_shop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvShopName.text = item.shop_name
        holder.tvDescription.text = "Area ${item.area_name}"
        holder.tvAmount.text = "Rs ${item.total}"
        holder.tvDate.text = item.sale_date

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, SaleHistoryActivity::class.java)
            intent.putExtra("ID", item.id)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvShopName: TextView = v.findViewById(R.id.tvShopName)
        val tvDescription: TextView = v.findViewById(R.id.tvDescription)
        val tvAmount: TextView = v.findViewById(R.id.tvAmount)
        val tvDate: TextView = v.findViewById(R.id.tvDate)
    }
}
