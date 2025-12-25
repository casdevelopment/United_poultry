package com.example.unitedpoultry.AdminArea.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminArea.EditAreaActivity
import com.example.unitedpoultry.AdminArea.model.AdminAreaModel
import com.example.unitedpoultry.AdminShopModule.AdminShopListActivity
import com.example.unitedpoultry.R
import com.google.android.material.button.MaterialButton

class AdminAreaAdapter(
    private val originalList: List<AdminAreaModel>
) : RecyclerView.Adapter<AdminAreaAdapter.AreaViewHolder>() {

    // Filtered list for search
    private var filteredList = originalList.toMutableList()

    // Card background colors (lighter shades)
    private val cardColors = listOf(
        R.color.primary9,
        R.color.sub_primary9,
        R.color.purple9,
        R.color.blue9
    )

    // Icon background colors (solid/darker)
    private val iconColors = listOf(
        R.color.primary,
        R.color.sub_primary,
        R.color.purple,
        R.color.blue
    )

    inner class AreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAreaName: TextView = itemView.findViewById(R.id.tvAreaName)
        val tvAreaAddress: TextView = itemView.findViewById(R.id.tvAreaAddress)
        val tvTotalShops: TextView = itemView.findViewById(R.id.tvTotalShops)
        val tvRiders: TextView = itemView.findViewById(R.id.tvRiders)
        val tvRecieveable: TextView = itemView.findViewById(R.id.tvRecieveable)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val iconContainer: CardView = itemView.findViewById(R.id.iconContainer)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val btnViewShops: MaterialButton = itemView.findViewById(R.id.btnViewShops)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_area, parent, false)
        return AreaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        val item = filteredList[position]

        // Set texts
        holder.tvAreaName.text = item.areaName
        holder.tvAreaAddress.text = item.address
        holder.tvTotalShops.text = item.totalShops
        holder.tvRiders.text = item.riders
        holder.tvRecieveable.text = item.recieveable

        // Set colors
        val cardColor = cardColors[position % cardColors.size]
        val iconColor = iconColors[position % iconColors.size]

        holder.iconContainer.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, cardColor)
        )

        holder.ivIcon.setColorFilter(
            ContextCompat.getColor(holder.itemView.context, iconColor)
        )

        holder.ivEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditAreaActivity::class.java)
            intent.putExtra("AREA_NAME", item.areaName)
            intent.putExtra("AREA_ADDRESS", item.address)
            context.startActivity(intent)
        }

        holder.btnViewShops.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminShopListActivity::class.java)
            intent.putExtra("AREA_NAME", item.areaName)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = filteredList.size

    // Function to filter list based on search
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.areaName.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }
}
