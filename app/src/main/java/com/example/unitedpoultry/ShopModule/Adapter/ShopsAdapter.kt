package com.example.unitedpoultry.ShopModule.Adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.ShopDetailsActivity
import com.example.unitedpoultry.ShopModule.ShopListActivity
import com.example.unitedpoultry.ShopModule.model.ShopsRecord

class ShopsAdapter(
    private val list: MutableList<ShopsRecord>
) : RecyclerView.Adapter<ShopsAdapter.ViewHolder>() {

    private val originalList = ArrayList(list)

    fun filter(query: String, status: String) {
        list.clear()
        for (item in originalList) {
            val matchSearch = item.name.contains(query, true) || item.address.contains(query, true)
            val matchStatus = status == "ALL" || item.status == status
            if (matchSearch && matchStatus) list.add(item)
        }
        notifyDataSetChanged()
    }

    fun countByStatus(status: String) = originalList.count { status == "ALL" || it.status == status }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]



        holder.tvName.text = item.name
        holder.tvAddress.text = item.address
        holder.tvLastVisit.text = "${item.lastVisit}"
        holder.tvRate.text = "Rs. ${item.rate}"
        holder.tvDues.text = "Rs. ${item.dues}"
        holder.tvStatus.text = item.status

        if (item.dues == 0) {
            holder.tvDues.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            holder.btnDeal.text = "View"
            holder.btnDeal.icon = ContextCompat.getDrawable(holder.itemView.context, R.drawable.eyevector)

        } else {
            holder.tvDues.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.btnDeal.text = "Collect"
            holder.btnDeal.icon = ContextCompat.getDrawable(holder.itemView.context, R.drawable.collectvector)

        }


        val bg = when(item.status) {
            "VISITED" -> R.drawable.status_bg_visited
            "OVERDUE" -> R.drawable.status_bg_visited
            else -> R.drawable.status_bg_visited
        }
        holder.tvStatus.setBackgroundResource(bg)

        holder.btnDeal.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ShopDetailsActivity::class.java)

            // Pass area name and address
            intent.putExtra("AREA_NAME", item.name)
            intent.putExtra("ADDRESS", item.address)

            context.startActivity(intent)
        }

        holder.tvInitials.text = getInitials(item.name)

        when (item.status) {

            "PENDING" -> {
                holder.tvInitials.setBackgroundResource(R.drawable.bg_initials_pending)
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))

                holder.tvLastVisit.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
            }

            "VISITED" -> {
                holder.tvInitials.setBackgroundResource(R.drawable.bg_initials_visited)

                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_visited)
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.sub_primary))


                holder.tvLastVisit.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.sub_primary))
            }

            "OVERDUE" -> {
                holder.tvInitials.setBackgroundResource(R.drawable.bg_initials_overdue)
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_overdue)
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.purple))

                holder.tvLastVisit.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.purple))
            }
        }

    }

    override fun getItemCount() = list.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvAddress: TextView = v.findViewById(R.id.tvAddress)
        val tvStatus: TextView = v.findViewById(R.id.tvStatus)
        val tvLastVisit: TextView = v.findViewById(R.id.tvLastVisit)
        val tvRate: TextView = v.findViewById(R.id.tvRate)
        val tvDues: TextView = v.findViewById(R.id.tvDues)
        val tvInitials: TextView = v.findViewById(R.id.tvInitials)
        val btnDeal: com.google.android.material.button.MaterialButton = v.findViewById(R.id.btnDeal)
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}


