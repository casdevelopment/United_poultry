package com.example.unitedpoultry.rider_home.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.adminproduct.model.Product
import com.example.unitedpoultry.databinding.ItemProductBinding

class EggPickUpAdapter(
    private val products: List<Product>,
    private val quantityMap: MutableMap<Int, Int>
) : RecyclerView.Adapter<EggPickUpAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        var textWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.binding.tvProductName.text = product.name

        // Remove old watcher before recycling views
        holder.textWatcher?.let { holder.binding.etQuantity.removeTextChangedListener(it) }

        val currentQty = quantityMap[product.id]
        holder.binding.etQuantity.setText(currentQty?.toString() ?: "")

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                var qty = s.toString().toIntOrNull() ?: 0

                // Limit Tray input to max 11
                if (product.name.contains("Tray", ignoreCase = true) && qty > 11) {
                    qty = 11
                    holder.binding.etQuantity.removeTextChangedListener(this)
                    holder.binding.etQuantity.setText(qty.toString())
                    holder.binding.etQuantity.setSelection(holder.binding.etQuantity.text.length)
                    holder.binding.etQuantity.addTextChangedListener(this)
                }

                if (qty <= 0) {
                    quantityMap.remove(product.id)
                } else {
                    quantityMap[product.id] = qty
                }
            }
        }

        holder.binding.etQuantity.addTextChangedListener(watcher)
        holder.textWatcher = watcher
    }

    override fun getItemCount(): Int = products.size
}