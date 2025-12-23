package com.example.unitedpoultry.Notification.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.Notification.model.NotificationModel
import com.example.unitedpoultry.R


class NotificationAdapter(private val notifications: List<NotificationModel>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    // 🎨 Background colors list
    private val bgColors = listOf(
        R.color.primary20,
        R.color.pink15,
        R.color.green15,
        R.color.sub_primary18
    )

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notifications[position]

        holder.ivIcon.setImageResource(item.imageRes)
        holder.tvTitle.text = item.title
        holder.tvMessage.text = item.message
        holder.tvTime.text = item.time

        // ✅ Set different background per item
        holder.itemView
            .findViewById<View>(R.id.card)
            .setBackgroundResource(bgColors[position % bgColors.size])
    }

    override fun getItemCount(): Int = notifications.size
}
