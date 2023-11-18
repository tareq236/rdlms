package com.impala.rdlms.delivery

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ItemProductListBinding
import com.impala.rdlms.delivery.model.Product

class ProductListAdapter(val context: Context) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    var list: MutableList<Product> = mutableListOf()

    fun addData(allCus: MutableList<Product>) {
        list.addAll(allCus)
        notifyDataSetChanged()
    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductListBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder) {
            binding.productName.text = item.material_name
            binding.totalQtyId.setText(item.quantity.toString())
            val qty = binding.totalQtyId.text.toString()
            val iQty = qty.toInt()
            val tp = item.tp
            val totalAmount = tp * iQty
            binding.totalAmountId.text = totalAmount.toString()

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
