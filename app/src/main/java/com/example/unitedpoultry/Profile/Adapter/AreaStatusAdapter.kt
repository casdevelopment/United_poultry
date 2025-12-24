package com.example.unitedpoultry.Profile.Adapter

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.Profile.model.AreaStatusModel
import com.example.unitedpoultry.R

class AreaStatusAdapter(
    private val context: Context,
    private val list: List<AreaStatusModel>
) : RecyclerView.Adapter<AreaStatusAdapter.ViewHolder>() {

    private val colorPairs = listOf(
        Pair(R.color.primary, R.color.primary20),
        Pair(R.color.sub_primary, R.color.sub_primary18),
        Pair(R.color.mint, R.color.mint20),
        Pair(R.color.red, R.color.red20)
    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAreaName: TextView = view.findViewById(R.id.tvAreaName)
        val tvPercentage: TextView = view.findViewById(R.id.tvPercentage)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_area_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvAreaName.text = item.areaName
        holder.tvPercentage.text = "${item.percentage}%"

        val drawable = holder.progressBar.progressDrawable.mutate() as LayerDrawable

        val (filledRes, unfilledRes) = colorPairs[position % colorPairs.size]

        drawable.findDrawableByLayerId(android.R.id.progress)
            .setTint(ContextCompat.getColor(context, filledRes))

        drawable.findDrawableByLayerId(android.R.id.background)
            .setTint(ContextCompat.getColor(context, unfilledRes))

        holder.progressBar.max = 100
        holder.progressBar.progress = item.percentage
    }


    override fun getItemCount(): Int = list.size
}
