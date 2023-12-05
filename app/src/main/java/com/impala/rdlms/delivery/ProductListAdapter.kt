package com.impala.rdlms.delivery

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.impala.rdlms.R
import com.impala.rdlms.db.ProductModel
import com.impala.rdlms.delivery.model.Product

class ProductListAdapter(private val list: List<Product>) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)

    }
    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Product) {
            val productName = itemView.findViewById<TextView>(R.id.productName)
            val totalQty = itemView.findViewById<TextView>(R.id.totalQty)
            val totalAmount = itemView.findViewById<TextView>(R.id.totalAmountId)
            val receivedQty = itemView.findViewById<EditText>(R.id.receivedQty)
            val returnQty = itemView.findViewById<EditText>(R.id.returnQty)
            val receivedAmount = itemView.findViewById<TextView>(R.id.receivedAmountId)
            productName.text = item.material_name
            totalQty.text = item.quantity.toString()
            totalAmount.text = item.net_val.toString()

            receivedQty.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()){
                        val totalQty = item.quantity

                        if(s.toString().toInt()<=totalQty){
                            receivedAmount.text = (receivedQty.text.toString().toDouble() * item.tp).toString()

                            val receivedQty = s.toString().toInt()
                            val result = totalQty - receivedQty
                            returnQty.setText(result.toString())
                        }else{
                            receivedQty.setText("")
                            receivedAmount.text = "0"
                            Toast.makeText(itemView.context,"Please input receive quantity under or qual of order quantity",Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        returnQty.setText("")
                        receivedAmount.text = "0"
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })



        }
    }






}
