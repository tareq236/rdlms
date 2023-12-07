package com.impala.rdlms.delivery

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.impala.rdlms.databinding.ActivityDeliveryRemainingBinding
import com.impala.rdlms.delivery.model.DeliveryData
import com.impala.rdlms.delivery.model.DeliveryResponse
import com.impala.rdlms.utils.ApiService
import com.impala.rdlms.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveryRemainingBinding
    private lateinit var loadingDialog: Dialog
    private lateinit var sessionManager: SessionManager
    lateinit var adapter: DeliveryAdapter
    private var deliveryType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryRemainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = DeliveryAdapter(this)
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading")
        sessionManager = SessionManager(this)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        deliveryType = sessionManager.deliveryType.toString()

        getDeliveryRemainingList()
    }

    private fun getDeliveryRemainingList() {
        showLoadingDialog()
        val apiService = ApiService.CreateApi1()
        apiService.getDeliveryRemainingList(sessionManager.userId.toString(),deliveryType).enqueue(object : Callback<DeliveryResponse> {
            override fun onResponse(call: Call<DeliveryResponse>, response: Response<DeliveryResponse>) {
                if (response.isSuccessful) {
                    val response = response.body()
                    if (response != null) {
                        if(response.success){
                            val dataList = response.result
                            adapter.addData(dataList as MutableList<DeliveryData>)
                            dismissLoadingDialog()
                        }else{
                            dismissLoadingDialog()
                            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Waring", response.message)
                        }
                    }else{
                        dismissLoadingDialog()
                        showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Response NULL value. Try later")
                    }
                } else {
                    dismissLoadingDialog()
                    showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Response failed. Try later")
                }
            }

            override fun onFailure(call: Call<DeliveryResponse>, t: Throwable) {
                dismissLoadingDialog()
                showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Network error")
            }
        })
    }

    private fun showDialogBox(type: Int, title: String, message: String, callback: (() -> Unit)? = null) {
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
}
