package com.example.unitedpoultry.rider_home.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.adminproduct.model.Product
import com.example.unitedpoultry.databinding.ItemProductBinding
import com.example.unitedpoultry.rider_home.model.PickedTodayProduct

class EggPickUpAdapter(
    private val products: List<PickedTodayProduct>,
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

        // Remove old watcher before setting text to avoid infinite listener callbacks
        holder.textWatcher?.let { holder.binding.etQuantity.removeTextChangedListener(it) }

        // Use value from quantityMap or default product quantity
        val currentQty = quantityMap[product.id] ?: if (product.picked_quantity > 0) product.picked_quantity else null
        holder.binding.etQuantity.setText(currentQty?.toString() ?: "")

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                var qty = s.toString().toIntOrNull() ?: 0

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