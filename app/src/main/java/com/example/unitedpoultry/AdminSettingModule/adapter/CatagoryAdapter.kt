package com.example.unitedpoultry.AdminSettingModule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.DataModel.TodayRateItem
import com.example.unitedpoultry.R

class CatagoryAdapter(
    private val list: List<TodayRateItem>
) : RecyclerView.Adapter<CatagoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_catagory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.itemName.text = "${item.product_name}/${item.packing}"
        holder.etRate.text = item.price.toString()
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName)
        val etRate: TextView = view.findViewById(R.id.etRate)
    }
}
