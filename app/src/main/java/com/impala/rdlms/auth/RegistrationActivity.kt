package com.impala.rdlms.auth

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.impala.rdlms.R
import com.impala.rdlms.auth.models.RegistrationRequest
import com.impala.rdlms.auth.models.RegistrationResponse

import com.impala.rdlms.databinding.ActivityRegistrationBinding
import com.impala.rdlms.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrationBinding
    private lateinit var loadingDialog: Dialog
    var userType = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView(){
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        binding.loginId.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.rbDeliveryAssistant.setOnClickListener {
            if(binding.rbDeliveryAssistant.isChecked){
                userType = "Delivery Assistant"
            }
        }
        binding.rbDriver.setOnClickListener {
            if(binding.rbDriver.isChecked){
                userType = "Driver"
            }
        }
        binding.resisterId.setOnClickListener {
            val fullName = binding.tilFullName.editText!!.text.toString()
            val phoneNumber = binding.tilMobileNumber.editText!!.text.toString()
            val sapCode = binding.tilSapCode.editText!!.text.toString()
            val password = binding.tilNewPassword.editText!!.text.toString()
            val confPassword = binding.tilConfPassword.editText!!.text.toString()


            if (isValidate()){
                if (password != confPassword) {
                    showDialogBoxForValidation(
                        SweetAlertDialog.WARNING_TYPE,
                        "Validation",
                        "Password not match"
                    )
                    return@setOnClickListener
                }
                if(userType.trim().isEmpty()){
                    showDialogBoxForValidation(
                        SweetAlertDialog.WARNING_TYPE,
                        "Validation",
                        "User Type Required"
                    )
                    return@setOnClickListener
                }
                val registrationRequest = RegistrationRequest(
                     sapCode,
                    fullName,
                    phoneNumber,
                    userType,
                    password
                )

                register(registrationRequest)
            }
        }

        setupListeners()
    }
    private fun setupListeners() {
        binding.edtFullName.addTextChangedListener(TextFieldValidation(binding.edtFullName))
        binding.edtSapCode.addTextChangedListener(TextFieldValidation(binding.edtSapCode))
        binding.edtMobileNumber.addTextChangedListener(TextFieldValidation(binding.edtMobileNumber))
        binding.edtNewPassword.addTextChangedListener(TextFieldValidation(binding.edtNewPassword))
        binding.edtConfPassword.addTextChangedListener(TextFieldValidation(binding.edtConfPassword))
    }
    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // checking ids of each text field and applying functions accordingly.
            when (view.id) {
                R.id.edtFullName -> {
                    validateFullName()
                }

                R.id.edtSapCode -> {
                    validateSapCode()
                }

                R.id.edtMobileNumber -> {
                    validateMobileNumber()
                }

                R.id.edtNewPassword -> {
                    validatePassword()
                }



                R.id.edtConfPassword -> {
                    validateConfirmPassword()
                }
            }
        }
    }
    private fun register(registerM: RegistrationRequest){
        val apiService = ApiService.CreateApi1()
        showLoadingDialog()
        // Make the API call
        apiService.userRegistration(registerM)
            .enqueue(object : Callback<RegistrationResponse> {
                override fun onResponse(
                    call: Call<RegistrationResponse>,
                    response: Response<RegistrationResponse>
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
                                    "Save Successfully "
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
                    call: Call<RegistrationResponse>,
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

    private fun showLoadingDialog() {
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }
    private fun isValidate(): Boolean =
        validateFullName() && validateMobileNumber()
                && validateSapCode()
                && validatePassword() && validateConfirmPassword()
    private fun validateFullName(): Boolean {
        if (binding.edtFullName.text.toString().trim().isEmpty()) {
            binding.tilFullName.error = "Required Field!"
            binding.edtFullName.requestFocus()
            return false
        } else {
            binding.tilFullName.isErrorEnabled = false
        }
        return true
    }
    private fun validateSapCode(): Boolean {
        if (binding.edtSapCode.text.toString().trim().isEmpty()) {
            binding.tilSapCode.error = "Required Field!"
            binding.edtSapCode.requestFocus()
            return false
        } else {
            binding.tilSapCode.isErrorEnabled = false
        }
        return true
    }

    private fun validatePassword(): Boolean {
        if (binding.edtNewPassword.text.toString().trim().isEmpty()) {
            binding.tilNewPassword.error = "Required Field!"
            binding.edtNewPassword.requestFocus()
            return false
        } else if (binding.edtNewPassword.text.toString().length < 3) {
            binding.tilNewPassword.error = "password can't be less than 3"
            binding.edtNewPassword.requestFocus()
            return false
        } else {
            binding.tilNewPassword.isErrorEnabled = false
        }
        return true
    }
    private fun validateConfirmPassword(): Boolean {
        if (binding.edtConfPassword.text.toString().trim().isEmpty()) {
            binding.tilConfPassword.error = "Required Field!"
            binding.edtConfPassword.requestFocus()
            return false
        } else if (binding.edtConfPassword.text.toString().length < 3) {
            binding.tilConfPassword.error = "password can't be less than 3"
            binding.edtConfPassword.requestFocus()
            return false
        } else {
            binding.tilConfPassword.isErrorEnabled = false
        }
        return true
    }
    private fun validateMobileNumber(): Boolean {
        if (binding.edtMobileNumber.text.toString().trim().isEmpty()) {
            binding.tilMobileNumber.error = "Required Field!"
            binding.edtMobileNumber.requestFocus()
            return false
        } else if (!isValidMobileNo(binding.edtMobileNumber.text.toString())) {
            binding.tilMobileNumber.isErrorEnabled = true
            binding.tilMobileNumber.error = "Enter Right number"
            binding.edtMobileNumber.requestFocus()
            return false
        } else if (binding.edtMobileNumber.text.toString().length != 11) {
            binding.tilMobileNumber.isErrorEnabled = true
            binding.tilMobileNumber.error = "Need minimum 11 digit"
            binding.edtMobileNumber.requestFocus()
            return false
        } else {
            binding.tilMobileNumber.isErrorEnabled = false
        }
        return true
    }

    private fun isValidMobileNo(bdNumberStr: String?): Boolean {

        val phoneUtil = PhoneNumberUtil.getInstance()
        val isValid: Boolean = try {

            val bdNumberProto = phoneUtil.parse(bdNumberStr, "BD")
            phoneUtil.isValidNumber(bdNumberProto) // returns true
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: $e")
            false
        }
        return isValid
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
                finish()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        sweetAlertDialog.show()
    }
    private fun showDialogBoxForValidation(
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
}