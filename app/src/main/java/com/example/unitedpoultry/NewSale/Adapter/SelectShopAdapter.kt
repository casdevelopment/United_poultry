package com.example.unitedpoultry.NewSale.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.NewSale.SaleFormActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.ShopModule.ShopDetailsActivity
import com.example.unitedpoultry.NewSale.model.ShopModel
import com.google.android.material.button.MaterialButton

class SelectShopAdapter(
    private val list: MutableList<ShopModel>
) : RecyclerView.Adapter<SelectShopAdapter.ViewHolder>() {

    // Color lists (loaded once)
    private lateinit var fullColors: List<Int>
    private lateinit var lightColors: List<Int>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_newsale_selectshop, parent, false)

        // Initialize colors once
        if (!::fullColors.isInitialized) {
            fullColors = listOf(
                ContextCompat.getColor(parent.context, R.color.primary),
                ContextCompat.getColor(parent.context, R.color.sub_primary),
                ContextCompat.getColor(parent.context, R.color.blue),
                ContextCompat.getColor(parent.context, R.color.purple)
            )

            lightColors = listOf(
                ContextCompat.getColor(parent.context, R.color.primary9),
                ContextCompat.getColor(parent.context, R.color.sub_primary9),
                ContextCompat.getColor(parent.context, R.color.blue9),
                ContextCompat.getColor(parent.context, R.color.purple9)
            )
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvShopName.text = item.name
        holder.tvShopAddress.text = item.address
        holder.tvInitials.text = getInitials(item.name)

        // Loop colors
        val index = position % fullColors.size

        // Apply colors
        holder.tvInitials.background.mutate().setTint(fullColors[index])
        holder.itemView.background.mutate().setTint(lightColors[index])

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, SaleFormActivity::class.java)
            intent.putExtra("name", item.name)
            intent.putExtra("address", item.address)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvShopName: TextView = v.findViewById(R.id.tvShopName)
        val tvShopAddress: TextView = v.findViewById(R.id.tvShopAddress)
        val tvInitials: TextView = v.findViewById(R.id.tvInitials)
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}".uppercase()
            else -> parts[0][0].uppercase()
        }
    }

    private val originalList = ArrayList(list) // copy of original data

    fun filter(query: String) {
        val lowerCaseQuery = query.lowercase().trim()
        list.clear()
        if (lowerCaseQuery.isEmpty()) {
            list.addAll(originalList)
        } else {
            val filtered = originalList.filter {
                it.name.lowercase().contains(lowerCaseQuery) ||
                        it.address.lowercase().contains(lowerCaseQuery)
            }
            list.addAll(filtered)
        }
        notifyDataSetChanged()
    }

}
