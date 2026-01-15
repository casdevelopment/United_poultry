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

import com.example.unitedpoultry.AdminShopModule.model.ShopModel
import com.example.unitedpoultry.R

class AdminShopListAdapter(
    private var originalList: MutableList<ShopModel>,
    private val areaId: Int
) : RecyclerView.Adapter<AdminShopListAdapter.ViewHolder>() {

    private var filteredList: MutableList<ShopModel> = originalList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_shop_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]

        holder.tvName.text = item.name
        holder.tvAddress.text = item.address
        holder.tvDiscount.text = item.discount_per_petti + "%"
        holder.statusText.text = if (item.is_active == true) "Active" else "Inactive"

        holder.tvTotalShops.text = "-"
        holder.tvReceivable.text = "-"

        when (item.is_active) {
            true -> {
                holder.statusLayout.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.context, R.color.green)
                    )
                holder.statusText.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.primary)
                )
            }

            false -> {
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
            intent.putExtra("ID", item.id)
            intent.putExtra("AREA_ID", areaId)

            context.startActivity(intent)
        }



        // set colors based on status...
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<ShopModel>) {
        originalList.clear()
        originalList.addAll(newList)
        filteredList = originalList.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isBlank()) {
            originalList.toMutableList()
        } else {
            originalList.filter {
                it.name.contains(query, true) || it.address.contains(query, true)
            }.toMutableList()
        }
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvAddress: TextView = v.findViewById(R.id.tvAddress)
        val tvDiscount: TextView = v.findViewById(R.id.tvDiscount)
        val statusText: TextView = v.findViewById(R.id.statusText)
        val tvTotalShops: TextView = v.findViewById(R.id.tvTotalShops)
        val tvReceivable: TextView = v.findViewById(R.id.tvReceivable)
        val statusLayout: LinearLayout = v.findViewById(R.id.statusLayout)

    }
}
