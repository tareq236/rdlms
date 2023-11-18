package com.impala.rdlms.delivery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ActivityDeliveryRemainingDetailsBinding
import com.impala.rdlms.databinding.ActivityProductListBinding
import com.impala.rdlms.delivery.model.DeliveryData
import com.impala.rdlms.delivery.model.Invoice
import com.impala.rdlms.delivery.model.Product

class ProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListBinding
    lateinit var adapter: ProductListAdapter
    var qty =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        initView()
    }

    private fun initView(){

        val deliveryDetailsString = this.intent.getStringExtra("product_list")
        val deliveryDetailsM = Gson().fromJson(deliveryDetailsString, Invoice::class.java)
        Log.d("product_list", deliveryDetailsString!!)
        val productList = deliveryDetailsM.product_list

        adapter = ProductListAdapter(this)
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)

        adapter.addData(productList as MutableList<Product>)

        try {
            var sumQty = 0
            var sumTotalA = 0.0

            for (i in productList.indices) {
                qty = productList[i].quantity
                val tp = productList[i].tp
                qty+= qty
                sumTotalA+=qty*tp
            }
            binding.totalQtyId.text = qty.toString()
            binding.totalAmountId.text = sumTotalA.toString()

        }catch (e:Exception){
            e.printStackTrace()
        }


        binding.allDelivered.setOnClickListener {
            try {
                var sumQty = 0
                var sumTotalA = 0.0

                for (i in productList.indices) {
                    qty = productList[i].quantity
                    val tp = productList[i].tp
                    qty+= qty
                    sumTotalA+=qty*tp
                }
                binding.totalQtyId.text = qty.toString()
                binding.totalAmountId.text = sumTotalA.toString()

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}