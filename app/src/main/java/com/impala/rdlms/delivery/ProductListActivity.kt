package com.impala.rdlms.delivery

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ActivityProductListBinding
import com.impala.rdlms.delivery.model.DeliveryList
import com.impala.rdlms.delivery.model.DeliverySave
import com.impala.rdlms.delivery.model.DeliverySaveResponse
import com.impala.rdlms.delivery.model.Invoice
import com.impala.rdlms.delivery.model.Product
import com.impala.rdlms.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductListActivity : AppCompatActivity(), ProductListAdapter.IAddDeliveryItem {
    private lateinit var binding: ActivityProductListBinding
    private val locationPermissionCode = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var adapter: ProductListAdapter
    var qty = 0
    var invoiceId = ""
    lateinit var transportArr: Array<String>
    lateinit var deliveryList: MutableList<DeliveryList>
    private lateinit var loadingDialog: Dialog
    var flag = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        flag = this.intent.getStringExtra("flag")!!
        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        if (!checkLocationPermission()) {
            requestLocationPermission()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initView()
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    private fun initView() {
        //hide layout
        if(flag=="cash"){
            binding.linTransportType.visibility = View.GONE
            binding.allReceivedId.visibility = View.GONE
            binding.cancelId.visibility = View.GONE
            binding.allDeliveredId.visibility = View.GONE
            binding.cashCollectionId.visibility = View.VISIBLE
            binding.linReceivedAmount.visibility = View.VISIBLE
        }else{
            binding.linTransportType.visibility = View.VISIBLE
            binding.allReceivedId.visibility = View.VISIBLE
            binding.cancelId.visibility = View.VISIBLE
            binding.allDeliveredId.visibility = View.VISIBLE
            binding.cashCollectionId.visibility = View.GONE
            binding.linReceivedAmount.visibility = View.GONE
        }


        invoiceId = this.intent.getStringExtra("invoice_id")!!
        val deliveryDetailsString = this.intent.getStringExtra("product_list")
        val deliveryDetailsM = Gson().fromJson(deliveryDetailsString, Invoice::class.java)
        val totalAmount = this.intent.getStringExtra("total_amount")
        binding.totalAmountId.text = totalAmount

        deliveryList = mutableListOf()
        val productList = deliveryDetailsM.product_list

        for (i in productList) {
            val deliveryItem =
                DeliveryList(i.matnr, i.batch, i.quantity, i.tp, i.vat, i.net_val, 0, 0.0, 0, 0.0)
            deliveryList.add(deliveryItem)
        }

        transportArr = resources.getStringArray(R.array.transportType)
        adapter = ProductListAdapter("regular", this,flag)
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        adapter.addData(productList as MutableList<Product>)

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

        binding.allDeliveredId.setOnClickListener {
            getCurrentLocation { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    if (validateInput(binding.actvTransportType.text.toString())) {
                        val deliveryList = DeliverySave(
                            billing_doc_no = deliveryDetailsM.billing_doc_no,
                            billing_date = deliveryDetailsM.billing_date,
                            route_code = deliveryDetailsM.route_code,
                            partner = deliveryDetailsM.partner,
                            gate_pass_no = deliveryDetailsM.gate_pass_no,
                            da_code = deliveryDetailsM.da_code.toString(),
                            vehicle_no = deliveryDetailsM.vehicle_no,
                            delivery_latitude = latitude.toString(),
                            delivery_longitude = longitude.toString(),
                            transport_type = binding.actvTransportType.text.toString(),
                            delivery_status = "Done",
                            last_status = "delivery",
                            type = "delivery",
                            deliverys = deliveryList
                        )

                        val apiService = ApiService.CreateApi1()
                        showLoadingDialog()
                        // Make the API call
                        apiService.saveDeliveryData(deliveryList)
                            .enqueue(object : Callback<DeliverySaveResponse> {
                                override fun onResponse(
                                    call: Call<DeliverySaveResponse>,
                                    response: Response<DeliverySaveResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        // Handle successful login response
                                        val response = response.body()
                                        if (response != null) {
                                            if (response.success) {
                                                dismissLoadingDialog()
                                                showFDialogBox(
                                                    SweetAlertDialog.SUCCESS_TYPE,
                                                    "SUCCESS",
                                                    "Save Successfully  "
                                                )

                                            } else {
                                                dismissLoadingDialog()
                                                showDialogBox(
                                                    SweetAlertDialog.WARNING_TYPE,
                                                    "Waring",
                                                    response.message
                                                )
                                            }
                                        } else {
                                            dismissLoadingDialog()
                                            showDialogBox(
                                                SweetAlertDialog.ERROR_TYPE,
                                                "Error",
                                                "Response NULL value. Try later"
                                            )
                                        }
                                    } else {
                                        dismissLoadingDialog()
                                        showDialogBox(
                                            SweetAlertDialog.ERROR_TYPE,
                                            "Error",
                                            "Response failed. Try later"
                                        )
                                    }
                                }

                                override fun onFailure(
                                    call: Call<DeliverySaveResponse>,
                                    t: Throwable
                                ) {
                                    dismissLoadingDialog()
                                    showDialogBox(
                                        SweetAlertDialog.ERROR_TYPE,
                                        "Error",
                                        "Network error"
                                    )
                                }
                            })
                    }
                } else {
                    showToast("Location data not available.")
                }
            }
        }

        binding.allReceivedId.setOnClickListener {
            val adapter = ProductListAdapter("all_received", this,flag)

            val linearLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.layoutManager = linearLayoutManager
            binding.recyclerView.adapter = adapter
            binding.recyclerView.setHasFixedSize(true)

            adapter.addData(productList as MutableList<Product>)

        }

        binding.allReturnId.setOnClickListener {
            val adapter = ProductListAdapter("all_return", this,flag)

            val linearLayoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.layoutManager = linearLayoutManager
            binding.recyclerView.adapter = adapter
            binding.recyclerView.setHasFixedSize(true)

            adapter.addData(productList as MutableList<Product>)

        }

        binding.cancelId.setOnClickListener {
            getCurrentLocation { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    if (validateInput(binding.actvTransportType.text.toString())) {
                        val deliveryList = DeliverySave(
                            billing_doc_no = deliveryDetailsM.billing_doc_no,
                            billing_date = deliveryDetailsM.billing_date,
                            route_code = deliveryDetailsM.route_code,
                            partner = deliveryDetailsM.partner,
                            gate_pass_no = deliveryDetailsM.gate_pass_no,
                            da_code = deliveryDetailsM.da_code.toString(),
                            vehicle_no = deliveryDetailsM.vehicle_no,
                            delivery_latitude = latitude.toString(),
                            delivery_longitude = longitude.toString(),
                            transport_type = binding.actvTransportType.text.toString(),
                            delivery_status = "Cancel",
                            last_status = "delivery",
                            type = "delivery",
                            deliverys = deliveryList
                        )

                        val apiService = ApiService.CreateApi1()
                        showLoadingDialog()
                        // Make the API call
                        apiService.saveDeliveryData(deliveryList)
                            .enqueue(object : Callback<DeliverySaveResponse> {
                                override fun onResponse(
                                    call: Call<DeliverySaveResponse>,
                                    response: Response<DeliverySaveResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        // Handle successful login response
                                        val response = response.body()
                                        if (response != null) {
                                            if (response.success) {
                                                dismissLoadingDialog()
                                                showFDialogBox(
                                                    SweetAlertDialog.SUCCESS_TYPE,
                                                    "SUCCESS",
                                                    "Save Successfully  "
                                                )

                                            } else {
                                                dismissLoadingDialog()
                                                showDialogBox(
                                                    SweetAlertDialog.WARNING_TYPE,
                                                    "Waring",
                                                    response.message
                                                )
                                            }
                                        } else {
                                            dismissLoadingDialog()
                                            showDialogBox(
                                                SweetAlertDialog.ERROR_TYPE,
                                                "Error",
                                                "Response NULL value. Try later"
                                            )
                                        }
                                    } else {
                                        dismissLoadingDialog()
                                        showDialogBox(
                                            SweetAlertDialog.ERROR_TYPE,
                                            "Error",
                                            "Response failed. Try later"
                                        )
                                    }
                                }

                                override fun onFailure(
                                    call: Call<DeliverySaveResponse>,
                                    t: Throwable
                                ) {
                                    dismissLoadingDialog()
                                    showDialogBox(
                                        SweetAlertDialog.ERROR_TYPE,
                                        "Error",
                                        "Network error"
                                    )
                                }
                            })
                    }
                } else {
                    showToast("Location data not available.")
                }
            }
        }



        binding.receivedAmountId.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {


            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    try {
                        val totalAmount = binding.totalAmountId.text.toString()
                        val iTotalAmount = totalAmount.toDouble()
                        val receivedAmount = iTotalAmount - s.toString().toDouble()
                        binding.dueAmountId.text=roundTheNumber(receivedAmount)
                    }catch (e:NumberFormatException){
                        e.printStackTrace()
                    }


                }

            }
        })
    }

    private fun showLoadingDialog() {
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun validateInput(transportType: String): Boolean {
        if (transportType.isEmpty()) {
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "Transport type is required")
            return false
        }

        return true
    }

    private fun showFDialogBox(
        type: Int,
        title: String,
        message: String,
        callback: (() -> Unit)? = null
    ) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmClickListener {
                it.dismissWithAnimation()
                callback?.invoke()
                val intent = Intent(this@ProductListActivity, DeliveryRemainingActivity::class.java)
                startActivity(intent)
            }
        sweetAlertDialog.show()
    }

    private fun showDialogBox(
        type: Int,
        title: String,
        message: String,
        callback: (() -> Unit)? = null
    ) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmClickListener {
                it.dismissWithAnimation()
                callback?.invoke()
            }
        sweetAlertDialog.show()
    }

    private fun getCurrentLocation(callback: OnSuccessListener<Location>) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(callback)
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Handle the case where permission is not granted
            showToast("Location permission not granted.")
        }
    }

    private fun showToast(message: String) {
        // Implement showToast method to display messages
    }

    override fun deliveryList(
        matnr: String,
        receivedQty: String,
        receiveAmountId: String,
        returnQty: String,
        returnAmountId: String
    ) {
        val itemToUpdate = deliveryList.find { it.matnr == matnr }
        itemToUpdate?.apply {
            delivery_quantity = receivedQty.toInt()
            delivery_net_val = receiveAmountId.toDouble()
            return_quantity = returnQty.toInt()
            return_net_val = returnAmountId.toDouble()
        }
    }

    private fun roundTheNumber(numInDouble: Double): String {

        return "%.2f".format(numInDouble)

    }
}
