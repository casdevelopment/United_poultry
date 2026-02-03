package com.example.unitedpoultry.AdminSettingModule.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unitedpoultry.AdminSettingModule.DataModel.TodayRateItem
import com.example.unitedpoultry.R


class TodayRateAdapter(
    private var originalList: MutableList<TodayRateItem>,
    private var priceChangeListener: OnPriceChangeListener
) : RecyclerView.Adapter<TodayRateAdapter.ViewHolder>() {

    private var filteredList: MutableList<TodayRateItem> = originalList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_today_rate_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = filteredList[position]


        holder.etRate.setText(item.price.toString())
        holder.tvProductName.text = item.product_name.toString()
        holder.tvPacking.text = "${item.packing} / ${item.eggs_count} eggs"


        // Set text change listener
        holder.etRate.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updatePrice(position, holder.etRate.text.toString().trim())
            }
        }

        // Add done/enter key listener
        holder.etRate.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updatePrice(position, holder.etRate.text.toString().trim())
                true
            } else {
                false
            }
        }

        holder.etRate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.isNotEmpty()){
                    updatePrice(position, holder.etRate.text.toString().trim())
                }


            }
        })

    }

    private fun updatePrice(position: Int, newPrice: String) {
        if (position in 0 until filteredList.size) {
            val item = filteredList[position]

            // Update in lists
            item.price = newPrice.toInt()
            originalList[position].price = newPrice.toInt()

            // Notify listener
            priceChangeListener.onPriceChanged(
                position,
                newPrice

            )
        }
    }

    override fun getItemCount(): Int = filteredList.size


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvProductName: TextView = v.findViewById(R.id.tvProductName)
        val tvPacking: TextView = v.findViewById(R.id.tvPacking)
        val etRate: EditText = v.findViewById(R.id.etRate)


    }

    interface OnPriceChangeListener {
        fun onPriceChanged(position: Int, newPrice: String)
    }


}
