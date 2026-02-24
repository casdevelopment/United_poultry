package com.example.unitedpoultry.waste_return.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.databinding.ItemProductBinding


class RiderReturnProductAdapter(
    private val products: List<Product>,
    private val quantityMap: MutableMap<Int, Int>,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<RiderReturnProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.tvProductName.text = product.product_name
        //holder.binding.tvPacking.text = "${product.remaining_quantity} remaining"

        // Quantity setup
        holder.binding.etQuantity.setText(quantityMap[product.product_id]?.toString() ?: "")
        holder.binding.etQuantity.addTextChangedListener { editable ->
            val enteredQty = editable.toString().toIntOrNull() ?: 0
            val qty = enteredQty.coerceAtMost(product.remaining_quantity)
            if (qty != enteredQty) {
                holder.binding.etQuantity.setText(qty.toString())
                holder.binding.etQuantity.setSelection(qty.toString().length)
            }
            quantityMap[product.product_id] = qty
            onDataChanged()
        }
    }


    override fun getItemCount(): Int = products.size
}
