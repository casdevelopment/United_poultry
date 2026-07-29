package com.example.unitedpoultry.NewSale.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.NewSale.model.RiderProductUI
import com.example.unitedpoultry.databinding.SaleProductItemBinding

class SaleProductAdapter(
    private val products: List<RiderProductUI>,
    private val quantityMap: MutableMap<Int, Int>,
    private val onTotalsChanged: (subtotal: Double, totalEggs: Int) -> Unit
) : RecyclerView.Adapter<SaleProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: SaleProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var textWatcher: TextWatcher? = null
    }

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
        holder.binding.tvPrice.text = "price: ${product.price}/${product.name}"

        // Remove old watcher before recycling
        holder.textWatcher?.let { holder.binding.etQuantity.removeTextChangedListener(it) }

        holder.binding.etQuantity.setText(
            quantityMap[product.id]?.toString() ?: ""
        )

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val enteredQty = s.toString().toIntOrNull() ?: 0

                var allowedQty = enteredQty

                if (product.name.equals("Petti", ignoreCase = true)) {
                    // Get current selected Trays (if any)
                    val currentTrayProductId = products.find { it.name.equals("Tray", ignoreCase = true) }?.id ?: -99
                    val selectedTrays = quantityMap[currentTrayProductId] ?: 0

                    val stockAvailableInTrays = product.remainingQty - selectedTrays
                    val maxPettisAllowed = (stockAvailableInTrays / 12).coerceAtLeast(0)

                    allowedQty = enteredQty.coerceAtMost(maxPettisAllowed)

                } else if (product.name.equals("Tray", ignoreCase = true)) {
                    // Get current selected Pettis (if any)
                    val currentPettiProductId = products.find { it.name.equals("Petti", ignoreCase = true) }?.id ?: -1
                    val selectedPettis = quantityMap[currentPettiProductId] ?: 0

                    val stockAvailableInTrays = product.remainingQty - (selectedPettis * 12)

                    // User can enter up to remaining trays, capped at 11 per business rule
                    val maxTraysAllowed = 11.coerceAtMost(stockAvailableInTrays).coerceAtLeast(0)

                    allowedQty = enteredQty.coerceAtMost(maxTraysAllowed)

                } else {
                    allowedQty = enteredQty.coerceAtMost(product.remainingQty)
                }

                // If entered quantity exceeds calculated allowed quantity, correct the UI text
                if (allowedQty != enteredQty) {
                    holder.binding.etQuantity.removeTextChangedListener(this)
                    holder.binding.etQuantity.setText(if (allowedQty > 0) allowedQty.toString() else "")
                    holder.binding.etQuantity.setSelection(holder.binding.etQuantity.text?.length ?: 0)
                    holder.binding.etQuantity.addTextChangedListener(this)
                }

                if (allowedQty > 0) {
                    quantityMap[product.id] = allowedQty
                } else {
                    quantityMap.remove(product.id)
                }

                // Recalculate totals
                var subtotal = 0.0
                var totalEggs = 0

                products.forEach { p ->
                    val q = quantityMap[p.id] ?: 0
                    subtotal += q * p.price

                    totalEggs += if (p.name.equals("Petti", ignoreCase = true)) {
                        q * 12
                    } else {
                        q
                    }
                }

                onTotalsChanged(subtotal, totalEggs)
            }
        }

        holder.binding.etQuantity.addTextChangedListener(watcher)
        holder.textWatcher = watcher
    }

    override fun getItemCount(): Int = products.size
}