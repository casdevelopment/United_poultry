package com.example.unitedpoultry.AdminRiderModule.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminRiderModule.AdminEditRiderActivity
import com.example.unitedpoultry.AdminRiderModule.AdminRiderDetailsActivity
import com.example.unitedpoultry.AdminRiderModule.model.AdminRiderModel
import com.example.unitedpoultry.R


class AdminRidersAdapter(
    private val list: MutableList<AdminRiderModel>
) : RecyclerView.Adapter<AdminRidersAdapter.RiderViewHolder>() {

    private val originalList = ArrayList(list)

    fun filter(query: String, status: String) {
        list.clear()
        for (item in originalList) {
            val matchSearch = item.name.contains(query, true) || item.joiningDate.contains(query, true)
            val matchStatus = status == "ALL" || item.status == status
            if (matchSearch && matchStatus) list.add(item)
        }
        notifyDataSetChanged()
    }

    fun countByStatus(status: String) = originalList.count { status == "ALL" || it.status == status }

    private val iconColors = listOf(
        R.color.primary,     // Icon background
        R.color.sub_primary,
        R.color.purple,
        R.color.blue
    )


    inner class RiderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvInitials: TextView = itemView.findViewById(R.id.tvInitials)
        val tvRiderName: TextView = itemView.findViewById(R.id.tvRiderName)
        val tvJoiningDate: TextView = itemView.findViewById(R.id.tvJoiningDate)
        val riderStatus: TextView = itemView.findViewById(R.id.riderStatus)
        val tvAreas: TextView = itemView.findViewById(R.id.tvAreas)
        val tvShops: TextView = itemView.findViewById(R.id.tvShops)
        val tvTodaySale: TextView = itemView.findViewById(R.id.tvTodaySale)
        val btnViewDetails: View = itemView.findViewById(R.id.btnViewDetails)
        val btnEdit: View = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_rider, parent, false)
        return RiderViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiderViewHolder, position: Int) {
        val item = list[position]


        holder.tvRiderName.text = item.name
        holder.tvJoiningDate.text = "Joined. ${item.joiningDate}"
        holder.riderStatus.text = item.status
        holder.tvAreas.text = item.areas.toString()
        holder.tvShops.text = item.shops.toString()
        holder.tvTodaySale.text = "Rs. ${item.todaySale.toString()}"

        holder.tvInitials.text = getInitials(item.name)

        val iconColor = iconColors[position % iconColors.size]

        holder.tvInitials.backgroundTintList =
            ContextCompat.getColorStateList(holder.itemView.context, iconColor)


        holder.btnViewDetails.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminRiderDetailsActivity::class.java)

            // Pass data
            intent.putExtra("RIDER_NAME", item.name)
            intent.putExtra("STATUS", item.status)
            intent.putExtra("AREAS", item.areas)
            intent.putExtra("SHOPS", item.shops)
            intent.putExtra("SALE", item.todaySale)
            intent.putExtra("initial", getInitials(item.name))

            context.startActivity(intent)
        }

        holder.btnEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminEditRiderActivity::class.java)

            // Pass data
            intent.putExtra("RIDER_NAME", item.name)

            context.startActivity(intent)
        }

        holder.btnViewDetails.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminRiderDetailsActivity::class.java)

            // Pass data
            intent.putExtra("RIDER_NAME", item.name)
            intent.putExtra("STATUS", item.status)
            intent.putExtra("AREAS", item.areas)
            intent.putExtra("SHOPS", item.shops)
            intent.putExtra("SALE", item.todaySale)
            intent.putExtra("initial", getInitials(item.name))

            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = list.size

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}
