package com.example.unitedpoultry.adminproduct.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.adminproduct.model.Product
import com.example.unitedpoultry.databinding.ItemProductBinding

class ProductAdapter(
    private val products: List<Product>,
    private val quantityMap: MutableMap<Int, Int>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = products[position]

        holder.binding.tvProductName.text = product.name

        holder.binding.etQuantity.setText(
            quantityMap[product.id]?.toString() ?: ""
        )

        holder.binding.etQuantity.addTextChangedListener {

            val qty = it.toString().toIntOrNull()

            if (qty == null || qty <= 0) {
                quantityMap.remove(product.id)
            } else {
                quantityMap[product.id] = qty
            }
        }
    }


//    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//        val product = products[position]
//        holder.binding.tvProductName.text = product.name
//        //holder.binding.tvPacking.text = "${product.packing} / ${product.eggs_count} eggs"
//
//        holder.binding.etQuantity.setText(quantityMap[product.id]?.toString())
//
////        holder.binding.etQuantity.addTextChangedListener {
////            val qty = it.toString().toIntOrNull() ?: 0
////            quantityMap[product.id] = qty
////        }
//        holder.binding.etQuantity.addTextChangedListener {
//
//            val qty = it.toString().toIntOrNull() ?: 0
//
//            val product = products[position]
//
//            if (product.name.contains("tray", true) && qty > 11) {
//                holder.binding.etQuantity.setText("11")
//                holder.binding.etQuantity.setSelection(holder.binding.etQuantity.text.length)
//                quantityMap[product.id] = 11
//            } else {
//                quantityMap[product.id] = qty
//            }
//        }
//    }

    override fun getItemCount(): Int = products.size
}
