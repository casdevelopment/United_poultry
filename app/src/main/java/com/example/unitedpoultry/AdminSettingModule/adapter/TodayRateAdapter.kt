package com.example.unitedpoultry.AdminSettingModule.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.DataModel.TodayRateItem
import com.example.unitedpoultry.R

class TodayRateAdapter(
    private var list: MutableList<TodayRateItem>,
    private val priceChangeListener: OnPriceChangeListener
) : RecyclerView.Adapter<TodayRateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_rate_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvProductName.text = item.product_name
        holder.tvPacking.text = "${item.packing} / ${item.eggs_count} eggs"

        holder.etRate.removeTextChangedListener(holder.watcher)

        // Format price to hide .0 if it's a whole number
        val displayPrice = item.price?.let { price ->
            if (price % 1.0 == 0.0) price.toLong().toString() else price.toString()
        } ?: ""

        holder.etRate.setText(displayPrice)

        holder.watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim()
                if (text.isNullOrEmpty()) return

                val price = text.toDoubleOrNull() ?: return

                priceChangeListener.onPriceChanged(
                    item.product_id,
                    price.toString()
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        holder.etRate.addTextChangedListener(holder.watcher)
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvProductName: TextView = v.findViewById(R.id.tvProductName)
        val tvPacking: TextView = v.findViewById(R.id.tvPacking)
        val etRate: EditText = v.findViewById(R.id.etRate)

        var watcher: TextWatcher? = null
    }

    interface OnPriceChangeListener {
        fun onPriceChanged(productId: Int, newPrice: String)
    }
}