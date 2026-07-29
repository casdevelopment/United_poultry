package com.example.unitedpoultry.History.Adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.History.model.DamageItems
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ItemDamageSaleHistoryBinding


class SaleHistoryDamageAdapter(
    private var items: List<DamageItems>,
    private val type: String
) : RecyclerView.Adapter<SaleHistoryDamageAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemDamageSaleHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemDamageSaleHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.binding.tvProductName.text = item.product_name
        holder.binding.tvQty.text = "Qty: ${item.qty}"
        //holder.binding.tvTotalEggs.text = "Total Eggs: ${item.total_eggs}"

        val colorRes = when (type) {
            "expire" -> R.color.pink_lite
            "return" -> R.color.orange_lite
            else -> R.color.white
        }

        holder.binding.root.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, colorRes)
        )
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<DamageItems>) {
        items = newList
        notifyDataSetChanged()
    }
}