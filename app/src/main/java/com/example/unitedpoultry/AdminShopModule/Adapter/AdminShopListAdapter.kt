package com.example.unitedpoultry.AdminShopModule.Adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminShopModule.AdminShopDetailsActivity
import com.example.unitedpoultry.AdminShopModule.AdminShopListActivity
import com.example.unitedpoultry.AdminShopModule.model.AdminShopModel
import com.example.unitedpoultry.R

class AdminShopListAdapter(
    private val originalList: MutableList<AdminShopModel>
) : RecyclerView.Adapter<AdminShopListAdapter.ViewHolder>() {

    private var filteredList: MutableList<AdminShopModel> = originalList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_shop_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]

        holder.tvName.text = item.name
        holder.tvAddress.text = item.address
        holder.tvTotalShops.text = item.total.toString()
        holder.tvDiscount.text = item.Discount
        holder.tvReceivable.text = "Rs. ${item.recieveable}"
        holder.statusText.text = item.status

        when (item.status.lowercase()) {
            "active" -> {
                holder.statusLayout.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.context, R.color.green)
                    )
                holder.statusText.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.primary)
                )
            }

            "inactive" -> {
                holder.statusLayout.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.context, R.color.black)
                    )
                holder.statusText.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.black60)
                )
            }

            else -> {
                holder.statusLayout.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.context, R.color.gray)
                    )
                holder.statusText.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.gray)
                )
            }
        }


        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AdminShopDetailsActivity::class.java)
            intent.putExtra("name", item.name)
            intent.putExtra("address", item.address)
            intent.putExtra("total", item.total)
            intent.putExtra("Discount", item.Discount)
            intent.putExtra("recieveable", item.recieveable)
            intent.putExtra("status", item.status)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isBlank()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.contains(query, true) ||
                        it.address.contains(query, true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    fun getFilteredCount(): Int = filteredList.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvAddress: TextView = v.findViewById(R.id.tvAddress)
        val tvTotalShops: TextView = v.findViewById(R.id.tvTotalShops)
        val tvDiscount: TextView = v.findViewById(R.id.tvDiscount)
        val tvReceivable: TextView = v.findViewById(R.id.tvReceivable)
        val statusText: TextView = v.findViewById(R.id.statusText)
        val statusLayout: LinearLayout = v.findViewById(R.id.statusLayout)
    }
}
