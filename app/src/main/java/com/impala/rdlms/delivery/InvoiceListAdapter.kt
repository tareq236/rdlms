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
import com.impala.rdlms.db.DatabaseHelper
import com.impala.rdlms.delivery.model.Invoice
import com.impala.rdlms.utils.SessionManager

class InvoiceListAdapter(val context: Context,val flag:String) :
    RecyclerView.Adapter<InvoiceListAdapter.ViewHolder>() {

    var list: MutableList<Invoice> = mutableListOf()

    private lateinit var sessionManager: SessionManager

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
        sessionManager = SessionManager(context)
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
            binding.txvInvoiceAmount.text = roundTheNumber(totalNetVal)

            if(sessionManager.deliveryType.toString() != "Remaining"){
                var dvTotalNetVal = 0.0
                var dvTotalQty = 0.0
                for (product in item.product_list) {
                    dvTotalNetVal += product.delivery_net_val
                    dvTotalQty += product.delivery_quantity
                }
                binding.llDeliveryReport.visibility = View.VISIBLE
                binding.txvDeliveredQty.text = dvTotalQty.toString()
                binding.txvDeliveredAmount.text = roundTheNumber(dvTotalNetVal)
            }

            binding.mcvItem.setOnClickListener {
                val gson = Gson()
                val jsonStringItem = gson.toJson(item)
                val intent = Intent(itemView.context, ProductListActivity::class.java)
                    .putExtra("product_list", jsonStringItem)
                    .putExtra("invoice_id", item.billing_doc_no)
                    .putExtra("total_amount", binding.txvInvoiceAmount.text.toString())
                    .putExtra("flag", flag)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun roundTheNumber(numInDouble: Double): String {

        return "%.2f".format(numInDouble)

    }
}
