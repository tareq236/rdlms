package com.impala.rdlms.delivery

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ActivityDeliveryRemainingDetailsBinding
import com.impala.rdlms.delivery.model.DeliveryData
import com.impala.rdlms.delivery.model.DeliveryResponse
import com.impala.rdlms.delivery.model.Invoice

class DeliveryRemainingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveryRemainingDetailsBinding
    lateinit var adapter: InvoiceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryRemainingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = InvoiceListAdapter(this)
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        val deliveryDetailsString = this.intent.getStringExtra("delivery_details")
        val deliveryDetailsM = Gson().fromJson(deliveryDetailsString, DeliveryData::class.java)
        Log.d("delv_details", deliveryDetailsString!!)
        val invoiceList = deliveryDetailsM.invoice_list



        val date = deliveryDetailsM.billing_date
        val routeName = deliveryDetailsM.route_name
        val daName = deliveryDetailsM.da_name
        val custName = deliveryDetailsM.customer_name
        val custAddress = deliveryDetailsM.customer_address
        val custMobile = deliveryDetailsM.customer_mobile

        binding.dateId.text = date
        binding.routeNameId.text = routeName
        binding.daNameId.text = daName
        binding.customerNameId.text = custName
        binding.customerAddressId.text = custAddress
        binding.customerMobileId.text = custMobile


        adapter.addData(invoiceList as MutableList<Invoice>)
    }
}
