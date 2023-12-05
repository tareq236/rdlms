package com.impala.rdlms.delivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ActivityProductListBinding
import com.impala.rdlms.delivery.model.DeliveryList
import com.impala.rdlms.delivery.model.Invoice

class ProductListActivity : AppCompatActivity(),ProductListAdapter.IAddDeliveryItem {
    private lateinit var binding: ActivityProductListBinding
    lateinit var adapter: ProductListAdapter
    var qty =0
    var invoiceId =""
    lateinit var transportArr: Array<String>
    lateinit var deliveryList:MutableList<DeliveryList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        initView()
    }

    private fun initView(){
        invoiceId = this.intent.getStringExtra("invoice_id")!!
        val deliveryDetailsString = this.intent.getStringExtra("product_list")
        val deliveryDetailsM = Gson().fromJson(deliveryDetailsString, Invoice::class.java)
        deliveryList = mutableListOf()
        val productList = deliveryDetailsM.product_list


        for (i in productList){
            val matnr = i.matnr
            val batch = i.batch
            val quantity = i.quantity
            val tp = i.tp
            val vat = i.vat
            val netVale = i.net_val

            val deliveryItem = DeliveryList(matnr,batch,quantity,tp,vat,netVale,0,0.0,0,0.0)
            deliveryList.add(deliveryItem)
        }

        transportArr = resources.getStringArray(R.array.transportType)
        adapter = ProductListAdapter(productList,this)
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        binding.txvBillDocNo.text = deliveryDetailsM.billing_doc_no
        binding.customerNameId.text = deliveryDetailsM.customer_name
        binding.customerAddressId.text = deliveryDetailsM.customer_address
        binding.txvGatePassNo.text = deliveryDetailsM.gate_pass_no
        binding.txvVehicleNo.text = deliveryDetailsM.vehicle_no


        binding.actvTransportType.setAdapter<ArrayAdapter<String>>(
            ArrayAdapter<String>(
                this,
                R.layout.dropdown_item, R.id.text1, transportArr
            )
        )

//        try {
//            var sumQty = 0
//            var sumTotalA = 0.0
//
//            for (i in productList.indices) {
//                qty = productList[i].quantity
//                val tp = productList[i].tp
//                qty+= qty
//                sumTotalA+=qty*tp
//            }
//
//
//        }catch (e:Exception){
//            e.printStackTrace()
//        }

        binding.allReceivedId.setOnClickListener {

        }

    }

    override fun deliveryList(matnrr:String,receivedQty:String,receiveAmountId:String,returnQty:String,returnAmountId:String) {


//        for (i in deliveryList){
//
//            if (i.matnr == matnrr) {
//                // Update delivery_quantity for items with matnr "140002"
//                i.copy(delivery_quantity = i.delivery_quantity )
//            } else {
//                i
//            }
//            val batch = i.batch
//            val quantity = i.quantity
//            val tp = i.tp
//            val vat = i.vat
//            val netVale = i.net_val
//
//            val deliveryItem = DeliveryList(matnr,batch,quantity,tp,vat,netVale,0,0.0,0,0.0)
//            deliveryList.add(deliveryItem)
//        }
    }
}
