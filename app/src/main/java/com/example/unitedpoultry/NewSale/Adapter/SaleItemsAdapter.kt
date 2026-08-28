package com.example.unitedpoultry.NewSale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.History.model.SaleItem
import com.example.unitedpoultry.NewSale.model.Sale

import com.example.unitedpoultry.databinding.ItemSaleProductBinding

class SaleItemsAdapter(
    private val items: List<Sale>
) : RecyclerView.Adapter<SaleItemsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSaleProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSaleProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvProductName.text = item.product_name
            tvPacking.text = item.packing
            tvQuantity.text = "Qty: ${item.qty}"
            tvPrice.text = "Rs ${item.price}  per ${item.product_name}"

            tvLineTotal.text = "Rs ${item.line_total}"
        }
    }

    override fun getItemCount(): Int = items.size
}