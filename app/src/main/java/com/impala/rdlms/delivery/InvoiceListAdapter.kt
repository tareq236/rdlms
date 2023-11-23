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
import com.impala.rdlms.databinding.ItemInvoiceListBinding
import com.impala.rdlms.delivery.model.Invoice

class InvoiceListAdapter(val context: Context) : RecyclerView.Adapter<InvoiceListAdapter.ViewHolder>() {

    var list: MutableList<Invoice> = mutableListOf()

    fun addData(allCus: MutableList<Invoice>) {
        list.addAll(allCus)
        notifyDataSetChanged()
    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemInvoiceListBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_invoice_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder) {
            var totalNetVal = 0.0
            var totalQty = 0.0
            for (product in item.product_list) {
                totalNetVal += product.net_val
                totalQty += product.quantity
            }

            binding.txvBillDocNo.text = item.billing_doc_no
            binding.txvDeliveryStatus.text = item.delivery_status
            binding.txvInvoiceQty.text = totalQty.toString()
            binding.txvInvoiceAmount.text = totalNetVal.toString()

            binding.mcvItem.setOnClickListener {
                val gson = Gson()
                val jsonStringItem = gson.toJson(item)
                val intent = Intent(itemView.context, ProductListActivity::class.java)
                    .putExtra("product_list",jsonStringItem)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
