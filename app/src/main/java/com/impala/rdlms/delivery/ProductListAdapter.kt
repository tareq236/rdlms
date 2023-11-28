package com.impala.rdlms.delivery

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ItemProductListBinding
import com.impala.rdlms.db.DatabaseHelper
import com.impala.rdlms.db.ProductModel
import com.impala.rdlms.delivery.model.Product

class ProductListAdapter(val context: Context,val click:MainClickManage) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    var list: MutableList<ProductModel> = mutableListOf()
    var db:DatabaseHelper = DatabaseHelper(context)

    fun addData(allCus: MutableList<ProductModel>) {
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
            binding.productName.text = item.prodName
            binding.totalQty.text = item.quantity
            val qty = binding.totalQty.text.toString()
            val iQty = qty.toInt()
            val tp = item.tp.toDouble()
            val totalAmount = tp * iQty
            binding.totalAmountId.text = totalAmount.toString()

            val receivedQty = item.receivedQty

            if(receivedQty.isNotEmpty()){
                binding.receivedQty.setText(receivedQty)
                val receivedAm = tp * receivedQty.toInt()
                binding.receivedAmountId.text = receivedAm.toString()
            }else{
                binding.receivedQty.setText("0")
            }


            binding.receivedQty.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    if(s.toString().isNotEmpty()){
                        db.updateReceivedQty(item.invoiceId,s.toString(),item.id!!)
                        click.doClick()
                    }

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface MainClickManage {
        fun doClick( )
    }
}
