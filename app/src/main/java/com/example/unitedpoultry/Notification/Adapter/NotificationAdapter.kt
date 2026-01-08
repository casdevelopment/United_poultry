package com.example.unitedpoultry.Notification.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
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

    private val cardColors = listOf(
        R.color.primary9,    // Card background
        R.color.sub_primary9,
        R.color.purple9,
        R.color.blue9
    )

    // 🎨 Icon background colors (solid/darker)
    private val iconColors = listOf(
        R.color.primary,     // Icon background
        R.color.sub_primary,
        R.color.purple,
        R.color.blue
    )

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val iconContainer: CardView = itemView.findViewById(R.id.iconContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notifications[position]
        holder.tvTitle.text = item.title
        holder.tvMessage.text = item.message
        holder.tvTime.text = item.time

       // val cardColor = cardColors[position % cardColors.size]
        val iconColor = iconColors[position % iconColors.size]

        holder.iconContainer.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, iconColor)
        )

        // ✅ Set different background per item
        holder.itemView
            .findViewById<View>(R.id.card)
            .setBackgroundResource(cardColors[position % cardColors.size])
    }

    override fun getItemCount(): Int = notifications.size
}
