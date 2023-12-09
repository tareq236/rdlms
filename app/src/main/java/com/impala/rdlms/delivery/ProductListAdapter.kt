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
import com.impala.rdlms.delivery.model.DeliveryData
import com.impala.rdlms.delivery.model.Product

class ProductListAdapter(private val flag: String, val clickManage: IAddDeliveryItem,val flag1:String) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    var list: MutableList<Product> = mutableListOf()

    fun addData(allData: MutableList<Product>) {
        list.addAll(allData)
        notifyDataSetChanged()
    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        // holder.bind(item)
        with(holder) {
            if (flag1=="cash"){
                binding.linReceivedQty.visibility = View.GONE
                binding.linReceivedAmount.visibility = View.GONE
            }else {
                binding.linReceivedQty.visibility = View.VISIBLE
                binding.linReceivedAmount.visibility = View.VISIBLE
            }


            if (flag == "regular") {
                binding.productName.text = item.material_name
                binding.totalQty.text = item.quantity.toString()
                binding.totalAmountId.text = item.net_val.toString()


                var isReceivedEdit: Boolean = false
                var isReturnEdit: Boolean = false

                binding.returnQty.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        isReturnEdit = true
                        if (s.toString().isNotEmpty()) {
                            val totalQty = item.quantity
                            if (s.toString().toInt() <= totalQty) {
                                binding.returnAmountId.text = (binding.returnQty.text.toString()
                                    .toDouble() * item.tp).toString()
                                val returnQty = s.toString().toInt()
                                val result = totalQty - returnQty
                                if (!isReceivedEdit) binding.receivedQty.setText(result.toString())
                                clickManage.deliveryList(
                                    item.matnr,
                                    s.toString(),
                                    binding.receivedAmountId.text.toString(),
                                    binding.returnQty.text.toString(),
                                    binding.returnAmountId.text.toString()
                                )
                            } else {
                                binding.returnQty.setText("")
                                binding.returnAmountId.text = "0"
                                Toast.makeText(
                                    itemView.context,
                                    "Please input return quantity under or qual of order quantity",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            if (!isReceivedEdit) binding.receivedQty.setText("")
                            binding.returnAmountId.text = "0"
                        }
                        isReceivedEdit = false

                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })

                binding.receivedQty.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        isReceivedEdit = true
                        if (s.toString().isNotEmpty()) {
                            val totalQty = item.quantity
                            if (s.toString().toInt() <= totalQty) {
                                binding.receivedAmountId.text = (binding.receivedQty.text.toString()
                                    .toDouble() * item.tp).toString()
                                val receivedQty = s.toString().toInt()
                                val result = totalQty - receivedQty
                                if (!isReturnEdit) binding.returnQty.setText(result.toString())
                                clickManage.deliveryList(
                                    item.matnr,
                                    s.toString(),
                                    binding.receivedAmountId.text.toString(),
                                    binding.returnQty.text.toString(),
                                    binding.returnAmountId.text.toString()
                                )
                            } else {
                                binding.receivedQty.setText("")
                                binding.receivedAmountId.text = "0"
                                Toast.makeText(
                                    itemView.context,
                                    "Please input receive quantity under or qual of order quantity",
                                    Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            if (!isReturnEdit) binding.returnQty.setText("")
                            binding.receivedAmountId.text = "0"
                        }
                        isReturnEdit = false
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
            } else if (flag == "all_received") {
                val invoiceQty = item.quantity
                binding.receivedQty.setText(invoiceQty.toString())
                binding.productName.text = item.material_name
                binding.totalQty.text = item.quantity.toString()
                binding.totalAmountId.text = item.net_val.toString()
                binding.receivedAmountId.text =
                    (binding.receivedQty.text.toString().toDouble() * item.tp).toString()
                val receivedQty = binding.receivedQty.text.toString().toInt()
                val result = invoiceQty - receivedQty
                binding.returnQty.setText(result.toString())

            } else if (flag == "all_return") {
                val totalQty = item.quantity
                binding.totalQty.text = totalQty.toString()
                binding.returnQty.setText(totalQty.toString())
                binding.returnAmountId.text = (binding.returnQty.text.toString()
                    .toDouble() * item.tp).toString()
                val returnQty = binding.returnQty.text.toString().toInt()
                val result = totalQty - returnQty
                binding.receivedQty.setText(result.toString())
            }


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductListBinding.bind(itemView)
    }

    interface IAddDeliveryItem {
        fun deliveryList(
            matnr: String,
            receivedQty: String,
            receiveAmountId: String,
            returnQty: String,
            returnAmountId: String
        )
    }

}
