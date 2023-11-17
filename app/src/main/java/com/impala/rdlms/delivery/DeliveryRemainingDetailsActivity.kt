package com.impala.rdlms.delivery

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ActivityDeliveryRemainingDetailsBinding
import com.impala.rdlms.delivery.model.DeliveryData
import com.impala.rdlms.delivery.model.DeliveryResponse

class DeliveryRemainingDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveryRemainingDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryRemainingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val deliveryDetailsString = this.intent.getStringExtra("delivery_details")
        val deliveryDetails = Gson().fromJson(deliveryDetailsString, DeliveryData::class.java)


    }
}
