package com.example.unitedpoultry.NewSale.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.databinding.ItemProductBinding

class RiderProductAdapter(
    private val products: List<Product>,
    private val quantityMap: MutableMap<Int, Int>,
    private val onSubtotalChanged: (subtotal: Double) -> Unit
) : RecyclerView.Adapter<RiderProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // Use new model fields
        holder.binding.tvProductName.text = product.product_name
        holder.binding.tvPacking.text = "${product.remaining_quantity} remaining"

        // Set previous quantity if any
        holder.binding.etQuantity.setText(quantityMap[product.product_id]?.toString() ?: "")

        holder.binding.etQuantity.addTextChangedListener { editable ->
            val enteredQty = editable.toString().toIntOrNull() ?: 0

            // Limit quantity to remaining_quantity
            val qty = enteredQty.coerceAtMost(product.remaining_quantity)
            if (qty != enteredQty) {
                // If user entered more than remaining, reset to max allowed
                holder.binding.etQuantity.setText(qty.toString())
                holder.binding.etQuantity.setSelection(qty.toString().length) // move cursor to end
            }

            quantityMap[product.product_id] = qty

            // Calculate subtotal
            var subtotal = 0.0
            products.forEach { p ->
                val q = quantityMap[p.product_id] ?: 0
                subtotal += q * (p.price.toDoubleOrNull() ?: 0.0)
            }

            onSubtotalChanged(subtotal)
        }
    }


    override fun getItemCount(): Int = products.size
}
