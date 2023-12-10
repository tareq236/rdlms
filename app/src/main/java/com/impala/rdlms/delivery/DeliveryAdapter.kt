package com.impala.rdlms.delivery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.impala.rdlms.R
import com.impala.rdlms.delivery.model.DeliveryData
import com.impala.rdlms.databinding.ItemDeliveryBinding
import java.text.DecimalFormat

class DeliveryAdapter(val context: Context, val flag: String) :
    RecyclerView.Adapter<DeliveryAdapter.ViewHolder>() {

    var list: MutableList<DeliveryData> = mutableListOf()

    fun filterList(filteredList: ArrayList<DeliveryData>) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    fun addData(allCus: MutableList<DeliveryData>) {
        list.addAll(allCus)
        notifyDataSetChanged()
    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemDeliveryBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_delivery, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder) {
            binding.txvCustomerName.text = item.customer_name
            binding.txvCustomerAddress.text = item.customer_address
            binding.txvTotalInvoice.text = item.invoice_list.size.toString()
            var totalNetVal = 0.0
            var totalQty = 0.0
            for (invoice in item.invoice_list) {
                for (product in invoice.product_list) {
                    totalNetVal += product.net_val
                    totalQty += product.quantity
                }
            }
            binding.txvQuantity.text = totalQty.toInt().toString()
            val decimalFormat = DecimalFormat("#.##")
            val roundedAmount = decimalFormat.format(totalNetVal).toDouble()
            binding.txvAmount.text = roundedAmount.toString()

            binding.mcvItem.setOnClickListener {
                val gson = Gson()
                val jsonStringItem = gson.toJson(item)
                val intent = Intent(itemView.context, DeliveryReportActivity::class.java)
                    .putExtra("delivery_details", jsonStringItem)
                    .putExtra("total_amount", binding.txvAmount.text.toString())
                    .putExtra("flag", flag)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
