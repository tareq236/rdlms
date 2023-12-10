package com.impala.rdlms.delivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.impala.rdlms.databinding.ActivityDeliveryRemainingDetailsBinding
import com.impala.rdlms.delivery.model.DeliveryData
import com.impala.rdlms.delivery.model.Invoice
import com.impala.rdlms.utils.SessionManager

class DeliveryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveryRemainingDetailsBinding
    lateinit var adapter: InvoiceListAdapter
    var flag=""
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryRemainingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        sessionManager = SessionManager(this)
        flag=this.intent.getStringExtra("flag")!!
        adapter = InvoiceListAdapter(this,flag)
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        val deliveryDetailsString = this.intent.getStringExtra("delivery_details")
        val deliveryDetailsM = Gson().fromJson(deliveryDetailsString, DeliveryData::class.java)
        val totalInvoice = this.intent.getStringExtra("total_amount")!!
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
        binding.totalAmountId.text = totalInvoice

        if(sessionManager.deliveryType.toString() == "DeliveryDone"){
            binding.llImageMap.visibility= View.GONE
            binding.gatePass.text= deliveryDetailsM.invoice_list[0].gate_pass_no
            binding.transportType.text= deliveryDetailsM.invoice_list[0].transport_type
            binding.vehicleNo.text= deliveryDetailsM.invoice_list[0].vehicle_no
        }else{
            binding.llImageMap.visibility= View.VISIBLE
            binding.llGatePass.visibility= View.GONE
            binding.llTransportType.visibility= View.GONE
            binding.llVehicleNo.visibility= View.GONE
        }

        adapter.addData(invoiceList as MutableList<Invoice>)
    }
}
