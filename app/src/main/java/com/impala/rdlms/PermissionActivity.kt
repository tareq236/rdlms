package com.impala.rdlms

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.impala.rdlms.attendance.MorningAttendanceActivity
import com.impala.rdlms.models.LoginResponse
import com.impala.rdlms.utils.ApiService
import com.impala.rdlms.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PermissionActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    private val locationPermissionCode = 1
    private val cameraPermissionCode = 2
    private lateinit var permissionStatusTextView: TextView
    private lateinit var requestAllButton: Button
    private lateinit var loadingDialog: Dialog
    private var isStartWork:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        sessionManager = SessionManager(this)
        permissionStatusTextView = findViewById(R.id.permissionStatusTextView)
        requestAllButton = findViewById(R.id.requestAllButton)
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        val apiService = ApiService.CreateApi1()
        val userId = sessionManager.userId
        showLoadingDialog()
        // Make the API call to get menu items
        apiService.getUserDetails(userId.toString()).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Handle successful login response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            saveLoginInfoToSharedPreferences(loginResponse)
                        }else{
                            dismissLoadingDialog()
                            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Waring", loginResponse.message)
                        }
                    }else{
                        dismissLoadingDialog()
                        showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Response NULL value. Try later")
                    }
                }else{
                    dismissLoadingDialog()
                    showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Response failed. Try later")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                dismissLoadingDialog()
                showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Network error")
            }
        })

    }

    private fun saveLoginInfoToSharedPreferences(result: LoginResponse) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("full_name", result.result.full_name)
        editor.putString("mobile_number", result.result.mobile_number)
        editor.apply()
        sessionManager.fullName = result.result.full_name
        sessionManager.mobileNumber = result.result.mobile_number
        isStartWork = result.is_start_work

        // Check and update permission statuses
        updatePermissionStatus()

        // Set click listener for the "Request All Permissions" button
        requestAllButton.setOnClickListener {
            requestPermissions()
        }
    }

    private fun updatePermissionStatus() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val cameraPermission = Manifest.permission.CAMERA

        val locationPermissionStatus = checkPermissionStatus(locationPermission)
        val cameraPermissionStatus = checkPermissionStatus(cameraPermission)

        val locationStatusText = "Location: $locationPermissionStatus\n"
        val cameraStatusText = "Camera: $cameraPermissionStatus\n"

        val combinedStatusText = locationStatusText + cameraStatusText
        permissionStatusTextView.text = combinedStatusText

        if (locationPermissionStatus == PermissionStatus.GRANTED && cameraPermissionStatus == PermissionStatus.GRANTED) {

            if (isStartWork){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, MorningAttendanceActivity::class.java))
                finish()
            }

        } else {
            requestAllButton.isEnabled = true
        }
    }

    private fun checkPermissionStatus(permission: String): PermissionStatus {
        val permissionStatus = ContextCompat.checkSelfPermission(this, permission)

        return when {
            permissionStatus == PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> PermissionStatus.DENIED
            else -> PermissionStatus.NEVER_ASK_AGAIN
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val cameraPermission = Manifest.permission.CAMERA

        if (checkPermissionStatus(locationPermission) != PermissionStatus.GRANTED) {
            permissionsToRequest.add(locationPermission)
        }
        if (checkPermissionStatus(cameraPermission) != PermissionStatus.GRANTED) {
            permissionsToRequest.add(cameraPermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                locationPermissionCode // Update with your request code
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode || requestCode == cameraPermissionCode) {
            // Check if all requested permissions are granted
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }){
                if (isStartWork){
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this, MorningAttendanceActivity::class.java))
                    finish()
                }
            } else {
                // Handle the case when permissions are not granted
                // You can show a message to the user or request permissions again
            }
        }
    }

    enum class PermissionStatus {
        GRANTED,
        DENIED,
        NEVER_ASK_AGAIN
    }

    private fun showDialogBox(type: Int, title: String, message: String, callback: (() -> Unit)? = null) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmClickListener {
                it.dismissWithAnimation()
                callback?.invoke()
            }
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
