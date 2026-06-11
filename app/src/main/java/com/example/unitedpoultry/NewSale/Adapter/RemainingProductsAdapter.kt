//package com.example.unitedpoultry.NewSale.Adapter
//
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.unitedpoultry.NewSale.model.Remaining
//import com.example.unitedpoultry.databinding.ItemProductBinding
//
//
//class RemainingProductsAdapter(
//    private var products: List<Remaining>,
//    private val quantityMap: MutableMap<String, Int>,
//    private val onQuantityChanged: (String, Int) -> Unit
//) : RecyclerView.Adapter<RemainingProductsAdapter.VH>() {
//
//    inner class VH(val binding: ItemProductBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
//        val binding = ItemProductBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return VH(binding)
//    }
//
////    override fun onBindViewHolder(holder: VH, position: Int) {
////
////        val item = products[position]
////        //val price = priceMap[item.name] ?: 0.0
////
////        holder.binding.tvProductName.text = item.name
////
////        holder.binding.etQuantity.setText(
////            quantityMap[item.name]?.toString() ?: ""
////        )
//
////        holder.binding.etQuantity.addTextChangedListener(object : TextWatcher {
////
////            override fun afterTextChanged(s: Editable?) {
////
////                val maxQty = item.quantity
////                var qty = s.toString().toIntOrNull() ?: 0
////
////                if (qty > maxQty) {
////                    qty = maxQty
////                    holder.binding.etQuantity.setText(maxQty.toString())
////                    holder.binding.etQuantity.setSelection(holder.binding.etQuantity.text.length)
////                }
////
////                if (qty <= 0) {
////                    quantityMap.remove(item.name)
////                } else {
////                    quantityMap[item.name] = qty
////                }
////
////                calculateTotal()
////            }
////
////            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
////            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
////        })
////    }
//
////    private fun calculateTotal() {
////
////        var total = 0.0
////
////        products.forEach {
////            val qty = quantityMap[it.name] ?: 0
////            val price = priceMap[it.name] ?: 0.0
////            total += qty * price
////        }
////
////        onTotalChanged(total)
////    }
//
////    override fun getItemCount(): Int = products.size
////
////    fun updateList(newList: List<Remaining>) {
////        products = newList
////        notifyDataSetChanged()
////    }
//}