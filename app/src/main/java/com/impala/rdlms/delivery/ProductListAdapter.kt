package com.impala.rdlms.delivery

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ItemProductListBinding
import com.impala.rdlms.delivery.model.Product
import com.impala.rdlms.utils.SessionManager

class ProductListAdapter(
    private val flag: String,
    val clickManage: IAddDeliveryItem,
    val flag1: String,
    var sessionManager: SessionManager
) :
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
            if (flag1 == "cash") {
                binding.linReceivedQty.visibility = View.GONE
                binding.linReceivedAmount.visibility = View.GONE
            } else {
                binding.linReceivedQty.visibility = View.VISIBLE
                binding.linReceivedAmount.visibility = View.VISIBLE
            }

            if (flag == "regular") {
                binding.productName.text = item.material_name
                binding.totalQty.text = item.quantity.toString()
                binding.totalAmountId.text = roundTheNumber(item.net_val + item.vat)
            } else if (flag == "all_received") {
                val invoiceQty = item.quantity
                binding.receivedQty.setText(invoiceQty.toString())
                binding.productName.text = item.material_name
                binding.totalQty.text = item.quantity.toString()
                binding.totalAmountId.text = roundTheNumber(item.net_val + item.vat)
                binding.receivedAmountId.text = roundTheNumber(item.net_val + item.vat)
                val receivedQty = binding.receivedQty.text.toString().toInt()
                val result = invoiceQty - receivedQty
                binding.returnQty.setText(result.toString())

            } else if (flag == "all_return") {
                binding.productName.text = item.material_name
                val totalQty = item.quantity
                binding.totalQty.text = totalQty.toString()
                binding.returnQty.setText(totalQty.toString())
                binding.totalAmountId.text = roundTheNumber(item.net_val + item.vat)
                binding.returnAmountId.text = roundTheNumber(item.net_val + item.vat)
                val returnQty = binding.returnQty.text.toString().toInt()
                val result = totalQty - returnQty
                binding.receivedQty.setText(result.toString())
            }

            var isReceivedEdit: Boolean = false
            var isReturnEdit: Boolean = false
            binding.returnQty.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    isReturnEdit = true
                    if (s.toString().isNotEmpty()) {
                        val totalQty = item.quantity
                        val perVat = item.vat / item.quantity;
                        if (s.toString().toInt() <= totalQty) {
                            if(item.return_quantity != 0){
                                if((s.toString().toInt()) > item.return_quantity){
                                    val returnQ = binding.returnQty.text.toString().toDouble();
                                    val perAmount = ((item.net_val + item.vat) / item.quantity)
                                    binding.returnAmountId.text = roundTheNumber(perAmount * returnQ.toInt())
                                    val returnQty = s.toString().toInt()
                                    val result = totalQty - returnQty
                                    if (!isReceivedEdit) binding.receivedQty.setText(result.toString())
                                    clickManage.deliveryList(
                                        item.matnr,
                                        binding.receivedQty.text.toString(),
                                        binding.receivedAmountId.text.toString(),
                                        binding.returnQty.text.toString(),
                                        binding.returnAmountId.text.toString(),
                                        item.id
                                    )
                                }else{
                                    binding.returnQty.setText("")
                                    binding.returnAmountId.text = "0"
                                    Toast.makeText(
                                        itemView.context,
                                        "You have already returned a quantity of ${item.return_quantity}.",
                                        Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }else{
                                val returnQ = binding.returnQty.text.toString().toDouble();
                                val perAmount = ((item.net_val + item.vat) / item.quantity)
                                binding.returnAmountId.text = roundTheNumber(perAmount * returnQ.toInt())
                                val returnQty = s.toString().toInt()
                                val result = totalQty - returnQty
                                if (!isReceivedEdit) binding.receivedQty.setText(result.toString())
                                clickManage.deliveryList(
                                    item.matnr,
                                    binding.receivedQty.text.toString(),
                                    binding.receivedAmountId.text.toString(),
                                    binding.returnQty.text.toString(),
                                    binding.returnAmountId.text.toString(),
                                    item.id
                                )
                            }
                        } else {
                            binding.returnQty.setText("")
                            binding.returnAmountId.text = "0"
                            Toast.makeText(
                                itemView.context,
                                "Ensure that the return quantity does not exceed the quantity specified in the invoice.",
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                    } else {
                        if (!isReceivedEdit) binding.receivedQty.setText("")
                        binding.returnAmountId.text = "0"
                        clickManage.deliveryList(item.matnr, "0", "0", "0", "0", item.id)
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
                        val perVat = item.vat / item.quantity
                        if (s.toString().toInt() <= totalQty) {
                            val receivedQ = binding.receivedQty.text.toString().toDouble();
                            val perAmount = ((item.net_val + item.vat) / item.quantity)
                            binding.receivedAmountId.text = roundTheNumber(receivedQ.toInt() * perAmount)
                            val receivedQty = s.toString().toInt()

                            val result = totalQty - receivedQty
                            if (!isReturnEdit) binding.returnQty.setText(result.toString())
                            clickManage.deliveryList(
                                item.matnr,
                                binding.receivedQty.text.toString(),
                                binding.receivedAmountId.text.toString(),
                                binding.returnQty.text.toString(),
                                binding.returnAmountId.text.toString(),
                                item.id
                            )
                        } else {
                            binding.receivedQty.setText("")
                            binding.receivedAmountId.text = "0"
                            Toast.makeText(
                                itemView.context,
                                "Ensure that the receive quantity does not exceed the quantity specified in the invoice.",
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

            if(sessionManager.deliveryType.toString() == "DeliveryDone"){
                binding.receivedQty.visibility = View.GONE
                binding.returnQty.visibility = View.GONE
                binding.tvReturnQty.visibility = View.VISIBLE
                binding.tvReceivedQty.visibility = View.VISIBLE
                val perVat = item.vat / item.quantity;
                val receivedTotalVat = item.delivery_quantity * perVat;
                val returnTotalVat = item.return_quantity * perVat;
                binding.receivedAmountId.text= roundTheNumber(item.delivery_net_val)
                binding.returnAmountId.text=roundTheNumber(item.return_net_val)

                binding.tvReceivedQty.text = item.delivery_quantity.toString()
                binding.tvReturnQty.text = item.return_quantity.toString()
            }else if(sessionManager.deliveryType.toString() == "CashDone" || sessionManager.deliveryType.toString() == "ReturnDone"){
                binding.receivedQty.visibility = View.GONE
                binding.returnQty.visibility = View.GONE
                binding.tvReturnQty.visibility = View.VISIBLE
                binding.tvReceivedQty.visibility = View.VISIBLE
                val perVat = item.vat / item.quantity;
                val receivedTotalVat = item.delivery_quantity * perVat;
                val returnTotalVat = item.return_quantity * perVat;
                binding.receivedAmountId.text= roundTheNumber(item.delivery_net_val)
                binding.returnAmountId.text=roundTheNumber(item.return_net_val)

                binding.tvReceivedQty.text = item.delivery_quantity.toString()
                binding.tvReturnQty.text = item.return_quantity.toString()
            }else {
                binding.tvReturnQty.visibility = View.GONE
                binding.tvReceivedQty.visibility = View.GONE
                if(item.quantity == item.return_quantity){
                    binding.receivedQty.visibility = View.GONE
                    binding.returnQty.visibility = View.GONE
                    binding.tvReturnQty.visibility = View.VISIBLE
                    binding.tvReturnQty.text = item.return_quantity.toString()
                    binding.returnAmountId.text=roundTheNumber(item.return_net_val)

                }else{
                    if(item.return_quantity != 0){
                        binding.tvReturnQty.visibility = View.VISIBLE
                        binding.tvReturnQty.text = item.return_quantity.toString()+" returned."
                        val perVat = item.vat / item.quantity;
                        val returnTotalVat = item.return_quantity * perVat;
                        binding.returnAmountId.text=roundTheNumber(item.return_net_val)
                    }
                    binding.receivedQty.visibility = View.VISIBLE
                    binding.returnQty.visibility = View.VISIBLE
                }
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
            returnAmountId: String,
            id: Int?
        )
    }

    private fun roundTheNumber(numInDouble: Double): String {

        return "%.2f".format(numInDouble)

    }

}
