package com.impala.rdlms.auth

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Button
import android.widget.EditText
import com.impala.rdlms.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.impala.rdlms.utils.SessionManager
import com.impala.rdlms.PermissionActivity
import com.impala.rdlms.R
import com.impala.rdlms.models.LoginRequest
import com.impala.rdlms.models.LoginResponse


class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var loadingDialog: Dialog
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        sessionManager = SessionManager(this)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateInput(username, password)) {
                // Call the login function here
                performLogin(username, password)
            }
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
//            usernameEditText.error = "Username is required"
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "Username is required")
            return false
        }

        if (password.isEmpty()) {
//            passwordEditText.error = "Password is required"
            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Validation", "Password is required")
            return false
        }

        return true
    }

    private fun performLogin(username: String, password: String) {
        // Create a Retrofit API service (assuming you have Retrofit set up)
        val apiService = ApiService.CreateApi1()

        // Create a login request
        val loginRequest = LoginRequest(username, password)

        showLoadingDialog()
        // Make the API call
        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    // Handle successful login response
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if(loginResponse.success){
                            dismissLoadingDialog()
                            // Save login information (e.g., in SharedPreferences)
                            saveLoginInfoToSharedPreferences(loginResponse)

                            val intent = Intent(this@LoginActivity, PermissionActivity::class.java)
                            startActivity(intent)
                            finish()
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

        // Save properties from the Result data class to SharedPreferences
        editor.putString("sap_id", result.result.sap_id.toString())
        editor.putString("full_name", result.result.full_name)
        editor.putString("mobile_number", result.result.mobile_number)
        editor.putString("user_type", result.result.user_type)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
        sessionManager.userId = result.result.sap_id
        sessionManager.fullName = result.result.full_name
        sessionManager.mobileNumber = result.result.mobile_number
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
