package com.example.unitedpoultry.rider_home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.RiderDashboardTotalpickedBinding

class RiderHomeStatsAdapter :
    ListAdapter<Pair<String, Int>, RiderHomeStatsAdapter.ViewHolder>(DiffCallback()) {

    private var attachedRecyclerViewId: Int = -1

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerViewId = recyclerView.id
    }

    inner class ViewHolder(
        val binding: RiderDashboardTotalpickedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<String, Int>) {
            binding.tvProductName.text = item.first
            binding.tvQuantity.text = item.second.toString()

            // Apply color based on which RecyclerView this adapter is attached to
            val colorRes = when (attachedRecyclerViewId) {

                R.id.rvTotalPicked -> R.color.gray_light

                R.id.rvRemaining -> R.color.primary20

                R.id.rvExpire -> R.color.pink_lite

                R.id.rvReturn -> R.color.orange_lite
//
//                R.id.rvLiquid -> R.color.blue

                else -> R.color.white
            }

            binding.root.setCardBackgroundColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RiderDashboardTotalpickedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Pair<String, Int>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, Int>,
            newItem: Pair<String, Int>
        ) = oldItem.first == newItem.first

        override fun areContentsTheSame(
            oldItem: Pair<String, Int>,
            newItem: Pair<String, Int>
        ) = oldItem == newItem
    }
}