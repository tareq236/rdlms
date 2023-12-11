package com.impala.rdlms.cash_collection

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ActivityCashCollectionBinding
import com.impala.rdlms.databinding.ActivityDeliveryRemainingBinding
import com.impala.rdlms.delivery.DeliveryAdapter
import com.impala.rdlms.delivery.model.DeliveryData
import com.impala.rdlms.delivery.model.DeliveryResponse
import com.impala.rdlms.utils.ApiService
import com.impala.rdlms.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class CashCollectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCashCollectionBinding
    private lateinit var loadingDialog: Dialog
    private lateinit var sessionManager: SessionManager
    lateinit var adapter: DeliveryAdapter
    private var cashCollectionType: String = ""
    lateinit var dataList: MutableList<DeliveryData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        dataList = mutableListOf()
        adapter = DeliveryAdapter(this, "cash")
        loadingDialog =
            SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading")
        sessionManager = SessionManager(this)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        if(sessionManager.deliveryType.toString() == "CashRemaining"){
            cashCollectionType = "Remaining"
            binding.toolbar.title = "Cash Collection Remaining"
        }else if(sessionManager.deliveryType.toString() == "CashDone"){
            cashCollectionType = "Done"
            binding.toolbar.title = "Cash Collection Done"
        }

        getDeliveryRemainingList()

        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.toString().isNotEmpty()) {
                    filter(s.toString())
                } else {
                    //showLoadingDialog()
                    adapter.clearData()
                    //getDeliveryRemainingList()
                    adapter.addData(dataList as MutableList<DeliveryData>)
                }

            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun getDeliveryRemainingList() {
        showLoadingDialog()
        val apiService = ApiService.CreateApi1()
        apiService.getCashCollectionRemainingList(
            sessionManager.userId.toString(),
            cashCollectionType
        ).enqueue(object :
            Callback<DeliveryResponse> {
            override fun onResponse(
                call: Call<DeliveryResponse>,
                response: Response<DeliveryResponse>
            ) {
                if (response.isSuccessful) {
                    val response = response.body()
                    if (response != null) {
                        if (response.success) {
                            dataList = response.result as MutableList<DeliveryData>
                            adapter.addData(dataList)
                            dismissLoadingDialog()
                        } else {
                            dismissLoadingDialog()
                            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Waring", response.message)
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

            override fun onFailure(call: Call<DeliveryResponse>, t: Throwable) {
                dismissLoadingDialog()
                showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Network error")
            }
        })
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
        sweetAlertDialog.show()
    }

    private fun showLoadingDialog() {
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

    fun filter(text: String) {

        val filteredList: ArrayList<DeliveryData> = ArrayList()

        //val courseAry : ArrayList<Course> = Helper.Companion.getVersionsList()

        for (eachItem in dataList) {
            if (eachItem.customer_name.toUpperCase(Locale.getDefault())
                    .contains(text.toUpperCase()) || eachItem.customer_name.toUpperCase(
                    Locale.getDefault()
                ).contains(text.toLowerCase())
            ) {
                filteredList.add(eachItem)
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filteredList);
    }

}
