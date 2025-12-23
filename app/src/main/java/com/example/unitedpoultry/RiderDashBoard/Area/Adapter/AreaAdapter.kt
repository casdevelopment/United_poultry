package com.example.unitedpoultry.RiderDashBoard.Area.Adapter


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.R
import com.example.unitedpoultry.RiderDashBoard.Area.model.AreaModel
import com.example.unitedpoultry.ShopModule.ShopListActivity

class AreaAdapter(
    private val list: List<AreaModel>
) : RecyclerView.Adapter<AreaAdapter.AreaViewHolder>() {

    // 🎨 Card background colors (lighter shades)
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

    inner class AreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTotalShops: TextView = itemView.findViewById(R.id.tvTotalShops)
        val tvVisitedToday: TextView = itemView.findViewById(R.id.tvVisitedToday)
        val tvPending: TextView = itemView.findViewById(R.id.tvPending)
        val cardLayout1: LinearLayout = itemView.findViewById(R.id.totalShopsLayout)
        val cardLayout2: LinearLayout = itemView.findViewById(R.id.visitedTodayLayout)
        val cardLayout3: LinearLayout = itemView.findViewById(R.id.pendingLayout)
        val iconContainer: CardView = itemView.findViewById(R.id.iconContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_area, parent, false)
        return AreaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        val item = list[position]

        // Set texts
        holder.tvTitle.text = item.areaName
        holder.tvMessage.text = item.city
        holder.tvTotalShops.text = item.totalShops
        holder.tvVisitedToday.text = item.visitedToday
        holder.tvPending.text = item.pending

        // Determine colors
        val cardColor = cardColors[position % cardColors.size]
        val iconColor = iconColors[position % iconColors.size]

        // Apply background colors
        holder.cardLayout1.setBackgroundResource(cardColor)
        holder.cardLayout2.setBackgroundResource(cardColor)
        holder.cardLayout3.setBackgroundResource(cardColor)

        holder.iconContainer.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, iconColor)
        )



        // Click to open ShopListActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ShopListActivity::class.java)
            intent.putExtra("AREA_NAME", item.areaName)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}
