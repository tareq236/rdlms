package com.impala.rdlms.delivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.impala.rdlms.databinding.ActivityProductListBinding
import com.impala.rdlms.delivery.model.Invoice

class ProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListBinding
    lateinit var adapter: ProductListAdapter
    var qty =0
    var invoiceId =""


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
        val productList = deliveryDetailsM.product_list

        adapter = ProductListAdapter(productList)
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
}
