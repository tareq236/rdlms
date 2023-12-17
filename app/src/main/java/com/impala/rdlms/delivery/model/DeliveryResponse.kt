package com.impala.rdlms.delivery.model

data class DeliveryResponse(
    val success: Boolean,
    val error: String,
    val message: String,
    val result: List<DeliveryData>
)

data class DeliveryData(
    val billing_date: String,
    val route_code: String,
    val route_name: String,
    val da_code: Int,
    val da_name: String,
    val partner: String,
    val customer_name: String,
    val customer_address: String,
    val customer_mobile: String,
    val gate_pass_no: String,
    val latitude: Double,
    val longitude: Double,
    val delivery_status: String,
    val invoice_list: List<Invoice>
)

data class Invoice(
    val id: Int?,
    val billing_doc_no: String,
    val billing_date: String,
    val route_code: String,
    val route_name: String,
    val da_code: Int,
    val da_name: String,
    val partner: String,
    val customer_name: String,
    val customer_address: String,
    val customer_mobile: String,
    val latitude: Double,
    val longitude: Double,
    val delivery_status: String,
    val cash_collection: Double,
    val cash_collection_status: String,
    val gate_pass_no: String,
    val vehicle_no: String,
    val transport_type: String?,
    val product_list: List<Product>
)

data class Product(
    val id: Int?,
    val matnr: String,
    var quantity: Int,
    val tp: Double,
    val vat: Double,
    var net_val: Double,
    val batch: String,
    val material_name: String,
    val brand_description: String,
    val brand_name: String,
    val delivery_quantity: Int,
    val delivery_net_val: Double,
    val return_quantity: Int,
    val return_net_val: Double
)

