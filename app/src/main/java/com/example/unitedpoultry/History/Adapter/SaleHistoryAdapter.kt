package com.example.unitedpoultry.History.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.History.model.SaleItems
import com.example.unitedpoultry.databinding.ItemSaleHistoryBinding


class SaleHistoryAdapter(
    private var items: List<SaleItems>
) : RecyclerView.Adapter<SaleHistoryAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemSaleHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemSaleHistoryBinding.inflate(
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
    //    holder.binding.tvEggs.text = "Eggs: ${item.total_eggs}"
        holder.binding.tvPrice.text = "Rs ${item.price}"
     //   holder.binding.tvLineTotal.text = "Total: ${item.line_total}"
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<SaleItems>) {
        items = newList
        notifyDataSetChanged()
    }
}