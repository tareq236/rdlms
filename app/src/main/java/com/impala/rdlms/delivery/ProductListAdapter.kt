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
import com.impala.rdlms.databinding.ItemProductListBinding
import com.impala.rdlms.db.ProductModel
import com.impala.rdlms.delivery.model.Product

class ProductListAdapter(private val list: List<Product>, val clickManage:IAddDeliveryItem) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
       // holder.bind(item)
        with(holder) {
            binding.productName.text = item.material_name
            binding.totalQty.text = item.quantity.toString()
            binding.totalAmountId.text = item.net_val.toString()

            binding.receivedQty.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()){
                        val totalQty = item.quantity

                        if(s.toString().toInt()<=totalQty){
                            binding.receivedAmountId.text = (binding.receivedQty.text.toString().toDouble() * item.tp).toString()

                            val receivedQty = s.toString().toInt()
                            val result = totalQty - receivedQty
                            binding.returnQty.setText(result.toString())


                            clickManage.deliveryList(item.matnr,s.toString(),binding.receivedAmountId.text.toString(),binding.returnQty.text.toString(),binding.returnAmountId.text.toString())

                        }else{
                            binding.receivedQty.setText("")
                            binding.receivedAmountId.text = "0"
                            Toast.makeText(itemView.context,"Please input receive quantity under or qual of order quantity",Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        binding.returnQty.setText("")
                        binding.receivedAmountId.text = "0"
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

        }




    }
    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductListBinding.bind(itemView)
    }



    interface IAddDeliveryItem{
        fun deliveryList(matnr:String,receivedQty:String,receiveAmountId:String,returnQty:String,returnAmountId:String)

    }

}
