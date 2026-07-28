package com.example.unitedpoultry.NewSale.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.NewSale.model.RiderProductUI
import com.example.unitedpoultry.databinding.ItemProductBinding
import com.example.unitedpoultry.databinding.SaleProductItemBinding

class SaleProductAdapter(
    private val products: List<RiderProductUI>,
    private val quantityMap: MutableMap<Int, Int>,
    private val onTotalsChanged: (subtotal: Double, totalEggs: Int) -> Unit
) : RecyclerView.Adapter<SaleProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: SaleProductItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = SaleProductItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.binding.tvProductName.text = product.name
        holder.binding.tvPrice.text = "price: ${product.price.toInt()}/${product.name}"

        holder.binding.etQuantity.setText(
            quantityMap[product.id]?.toString() ?: ""
        )

        holder.binding.etQuantity.addTextChangedListener {
            val enteredQty = it.toString().toIntOrNull() ?: 0

            // 1. Determine maximum allowed limit for this specific product
            val maxAllowed = if (product.name.equals("Tray", ignoreCase = true)) {
                11.coerceAtMost(product.remainingQty) // Cap at 11 or remainingQty, whichever is lower
            } else {
                product.remainingQty
            }

            // 2. Coerce user input to maxAllowed
            val qty = enteredQty.coerceAtMost(maxAllowed)

            if (qty != enteredQty) {
                holder.binding.etQuantity.setText(qty.toString())
                holder.binding.etQuantity.setSelection(qty.toString().length)
            }

            quantityMap[product.id] = qty

            var subtotal = 0.0
            var totalEggs = 0

            products.forEach { p ->
                val q = quantityMap[p.id] ?: 0
                subtotal += q * p.price
                totalEggs += q
            }

            onTotalsChanged(subtotal, totalEggs)
        }
    }

    override fun getItemCount(): Int = products.size
}