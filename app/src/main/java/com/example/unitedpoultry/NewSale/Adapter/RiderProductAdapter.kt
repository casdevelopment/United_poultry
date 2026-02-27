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
    private val discountPerPati: Double, // pass discount from Activity
    private val onTotalsChanged: (subtotal: Double, discount: Double, total: Double, totalEggs: Int) -> Unit
) : RecyclerView.Adapter<RiderProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.binding.tvProductName.text = product.product_name
        holder.binding.etQuantity.setText(quantityMap[product.product_id]?.toString() ?: "")

        holder.binding.etQuantity.addTextChangedListener { editable ->
            val enteredQty = editable.toString().toIntOrNull() ?: 0
            val qty = enteredQty.coerceAtMost(product.remaining_quantity)
            if (qty != enteredQty) {
                holder.binding.etQuantity.setText(qty.toString())
                holder.binding.etQuantity.setSelection(qty.toString().length)
            }

            quantityMap[product.product_id] = qty

            // Calculate totals
            calculateTotals()
        }
    }

    private fun calculateTotals() {
        var subtotal = 0.0
        var totalEggs = 0
        var totalPatiEquivalent = 0.0  // total in pati

        products.forEach { p ->
            val qty = quantityMap[p.product_id] ?: 0
            subtotal += (p.price.toDoubleOrNull() ?: 0.0) * qty
            totalEggs += qty * p.eggs_count

            // Calculate pati equivalent
            if (p.product_name.equals("peti", ignoreCase = true)) {
                totalPatiEquivalent += qty.toDouble()
            } else if (p.product_name.equals("tray", ignoreCase = true)) {
                totalPatiEquivalent += qty.toDouble() / 12.0
            }
        }

        // Discount calculation
        val discount = (totalPatiEquivalent.toInt() * discountPerPati).toDouble()

        // Total after discount
        val total = (subtotal - discount).coerceAtLeast(0.0)

        // Pass to activity
        onTotalsChanged(subtotal, discount, total, totalEggs)
    }

    override fun getItemCount(): Int = products.size
}