package com.impala.rdlms.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ActivityEditProfileBinding
import com.impala.rdlms.databinding.ActivityRegistrationBinding
import com.impala.rdlms.utils.SessionManager

class EditProfileActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        sessionManager = SessionManager(this)

        val fullName = sessionManager.fullName
        val phoneNumber = sessionManager.mobileNumber
        val sapCode = sessionManager.userId
        val userType = sessionManager.userType

        binding.edtFullName.editText!!.setText(fullName)
        binding.edtMobileNumber.editText!!.setText(phoneNumber)
        binding.edtSapCode.editText!!.setText(sapCode.toString())
        if (userType == "Delivery Assistant") {
            binding.rbDeliveryAssistant.isChecked = true
        } else {
            binding.rbDriver.isChecked = true
        }

    }
}