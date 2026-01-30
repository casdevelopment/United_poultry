package com.example.unitedpoultry.NewSale.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.NewSale.model.Product
import com.example.unitedpoultry.databinding.ItemRiderProductHorizontalBinding

class RiderProductHorizontalAdapter :
    ListAdapter<Product, RiderProductHorizontalAdapter.ProductViewHolder>(ProductDiffCallback()) {

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) =
            oldItem.product_name == newItem.product_name // Use unique property
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }

    inner class ProductViewHolder(val binding: ItemRiderProductHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProductViewHolder(
            ItemRiderProductHorizontalBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.binding.tvProductName.text = product.product_name
        holder.binding.tvPacking.text = "${product.remaining_quantity} remaining"
    }
}
