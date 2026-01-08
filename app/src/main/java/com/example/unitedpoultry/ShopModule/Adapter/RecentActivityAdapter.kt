package com.example.unitedpoultry.ShopModule.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.model.RecentActivityModel

class RecentActivityAdapter(
    private val context: Context,
    private val activityList: List<RecentActivityModel>
) : RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivType: ImageView = itemView.findViewById(R.id.ivType)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvMethod: TextView = itemView.findViewById(R.id.tvMethod)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recent_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activityList[position]

        holder.tvTitle.text = activity.title
        holder.tvDateTime.text = activity.dateTime
        holder.tvMethod.text = activity.type

        if (activity.type == "cash") {
            holder.tvAmount.text = "-Rs ${activity.amount}"
            holder.ivType.setImageResource(R.drawable.cardsubprimarysvg)
            holder.tvAmount.setTextColor(
                context.getColor(R.color.primary)
            )
        } else {
            holder.tvAmount.text = "Rs ${activity.amount}"
            holder.ivType.setImageResource(R.drawable.boxprimarysvg)
            holder.tvAmount.setTextColor(
                context.getColor(R.color.black)
            )
        }
    }

    override fun getItemCount(): Int = activityList.size
}

