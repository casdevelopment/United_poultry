package com.example.unitedpoultry.NewSale.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.NewSale.model.SaleProduct
import com.example.unitedpoultry.databinding.ItemProductBinding

class ShowPickedProductSaleAdapter(
    private val products: List<SaleProduct>,
    private val discountPerPeti: Double,
    private val quantityMap: MutableMap<Int, Int>,
    private val onTotalsChanged: (subtotal: Double, discount: Double, total: Double,productQuantities: Map<String, Int>) -> Unit
) : RecyclerView.Adapter<ShowPickedProductSaleAdapter.VH>() {

    inner class VH(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = products[position]
        holder.binding.tvProductName.text = product.name

        // Remove previous TextWatcher if exists
        holder.binding.etQuantity.tag?.let { oldWatcher ->
            if (oldWatcher is TextWatcher) {
                holder.binding.etQuantity.removeTextChangedListener(oldWatcher)
            }
        }

        // Set initial quantity
        holder.binding.etQuantity.setText(quantityMap[product.id]?.toString() ?: "")

        // Safe TextWatcher
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val enteredQty = s.toString().toIntOrNull() ?: 0
                val qty = enteredQty.coerceAtMost(product.remainingQty)

                // Remove watcher temporarily to avoid recursion
                holder.binding.etQuantity.removeTextChangedListener(this)

                // Update text safely
                holder.binding.etQuantity.setText(qty.toString())
                holder.binding.etQuantity.setSelection(qty.toString().length)

                // Save quantity
                quantityMap[product.id] = qty

                // Reattach watcher
                holder.binding.etQuantity.addTextChangedListener(this)
                holder.binding.etQuantity.tag = this

                // Recalculate totals
                calculateTotals()
            }
        }

        // Attach watcher and save in tag
        holder.binding.etQuantity.addTextChangedListener(watcher)
        holder.binding.etQuantity.tag = watcher
    }

    private fun calculateTotals() {
        var subtotal = 0.0
        var totalPetiEquivalent = 0.0
        val productQuantities = mutableMapOf<String, Int>()

        products.forEach { p ->
            val qty = quantityMap[p.id] ?: 0
            subtotal += p.price * qty
            productQuantities[p.name.lowercase()] = qty

            // Peti equivalent for discount
            if (p.name.equals("peti", true)) {
                totalPetiEquivalent += qty.toDouble()
            } else if (p.name.equals("tray", true)) {
                // Integer division to count only full Peti equivalents
                totalPetiEquivalent += (qty / 12) // integer division
            }
        }

        val discount = totalPetiEquivalent * discountPerPeti
        val total = (subtotal - discount).coerceAtLeast(0.0)

        // Send totals back to Activity
        onTotalsChanged(subtotal, discount, total,productQuantities)
    }

    override fun getItemCount(): Int = products.size
}