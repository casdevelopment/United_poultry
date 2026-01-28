package com.example.unitedpoultry.exchange_return.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.databinding.ExchangeItemProductBinding


class ExchangeProductAdapter(
    private val products: List<Product>,
    private val quantityMap: MutableMap<Int, Int>,
    private val actionMap: MutableMap<Int, String>,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<ExchangeProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ExchangeItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ExchangeItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.tvProductName.text = product.product_name
        holder.binding.tvPacking.text = "${product.remaining_quantity} remaining"

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

        // --- Default action setup: select "Exchange" if not set ---
        if (!actionMap.containsKey(product.product_id)) {
            actionMap[product.product_id] = "exchange"
        }

        // Radio buttons setup
        holder.binding.rgReturnExchange.setOnCheckedChangeListener { _, checkedId ->
            val action = when (checkedId) {
                holder.binding.rbReturn.id -> "damage"
                holder.binding.rbExchange.id -> "exchange"
                else -> ""
            }
            actionMap[product.product_id] = action
            onDataChanged()
        }

        // Restore previous selection (or default to Exchange)
        when (actionMap[product.product_id]) {
            "damage" -> holder.binding.rbReturn.isChecked = true
            "exchange" -> holder.binding.rbExchange.isChecked = true
        }
    }


    override fun getItemCount(): Int = products.size
}
