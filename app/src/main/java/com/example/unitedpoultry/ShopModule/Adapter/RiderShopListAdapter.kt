package com.example.unitedpoultry.ShopModule.Adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.example.unitedpoultry.AdminShopModule.model.ShopModel
import com.example.unitedpoultry.Collection.CollectionformActivity
import com.example.unitedpoultry.NewSale.SaleFormActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.ShopDetailsActivity
import com.google.android.material.button.MaterialButton

class RiderShopListAdapter(
    private var originalList: MutableList<ShopModel>,
    private val areaId: Int
) : RecyclerView.Adapter<RiderShopListAdapter.ViewHolder>() {

    private var filteredList: MutableList<ShopModel> = originalList.toMutableList()

    private val iconColors = listOf(
        R.color.primary,
        R.color.sub_primary,
        R.color.purple,
        R.color.blue
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]

        holder.tvName.text = item.name
        holder.tvAddress.text = item.address
        holder.tvDiscount.text = item.discount_per_petti + "%"
        holder.statusText.text = if (item.is_active == true) "Active" else "Inactive"

        holder.tvInitials.text = getInitials(item.name)

        val iconColor = iconColors[position % iconColors.size]
//
//        holder.tvTotalShops.text = "-"
//        holder.tvReceivable.text = "-"

        when (item.is_active) {
            true -> {
                holder.statusLayout.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.context, R.color.green)
                    )
                holder.statusText.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.primary)
                )

                holder.tvInitials.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, iconColor)

                holder.statusText.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.mint)
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

                holder.tvInitials.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.black17)

                holder.statusText.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.black44)
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
            val intent = Intent(context, ShopDetailsActivity::class.java)
            intent.putExtra("ID", item.id)
            context.startActivity(intent)
        }

        holder.btnNewSale.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, SaleFormActivity::class.java)
            intent.putExtra("ID", item.id)
            intent.putExtra("NAME", item.name)
            intent.putExtra("ADDRESS", item.address)
            intent.putExtra("DISCOUNT", item.discount_per_petti)
            context.startActivity(intent)
        }

        holder.btnCollect.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, CollectionformActivity::class.java)
            intent.putExtra("ID", item.id)
            intent.putExtra("NAME", item.name)
            intent.putExtra("ADDRESS", item.address)
            intent.putExtra("DISCOUNT", item.discount_per_petti)
            context.startActivity(intent)
        }

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
        val tvInitials: TextView = v.findViewById(R.id.tvInitials)
        val statusLayout: LinearLayout = v.findViewById(R.id.statusLayout)
        val btnNewSale: MaterialButton = v.findViewById(R.id.btnNewSale)
        val btnCollect: MaterialButton = v.findViewById(R.id.btnCollect)

    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }
}
