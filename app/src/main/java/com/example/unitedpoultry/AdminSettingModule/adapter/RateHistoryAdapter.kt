package com.example.unitedpoultry.AdminSettingModule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.DataModel.RatesData
import com.example.unitedpoultry.R

class RateHistoryAdapter(
    private val  originalList: MutableList<RatesData>,
) : RecyclerView.Adapter<RateHistoryAdapter.ViewHolder>() {

    private var filteredList: MutableList<RatesData> = originalList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rate_history_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]

        holder.tVDate.text = item.date ?: ""
        val name = item.product_name ?: "N/A"
        val price = item.price ?: "0.00"

        holder.tVPrice.text = "$price / $name"
    }



    override fun getItemCount(): Int = filteredList.size


    fun updateList(newList: MutableList<RatesData>) {
        originalList.clear()
        originalList.addAll(newList)

        filteredList.clear()
        filteredList.addAll(newList)

        notifyDataSetChanged()
    }


    fun filter(query: String) {
        if (query.isBlank()) {
            filteredList = originalList.toMutableList()
        } else {
            filteredList = originalList.filter { item ->
                item.date?.contains(query, ignoreCase = true) == true ||
                        item.product_name?.contains(query, ignoreCase = true) == true
            }.toMutableList()
        }
        notifyDataSetChanged()
    }



    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tVDate: TextView = v.findViewById(R.id.tVDate)
        val tVPrice: TextView = v.findViewById(R.id.tVPrice)
    }


}
