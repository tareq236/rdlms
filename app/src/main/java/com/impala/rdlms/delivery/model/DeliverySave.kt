package com.impala.rdlms.delivery.model

data class DeliverySave(
    val deliverys: List<DeliveryList>
)

data class DeliveryList(
    val matnr: String,
    val batch: String,
    val quantity: Int,
    val tp: Double,
    val vat: Double,
    val net_val: Double,
    val delivery_quantity: Int,
    val delivery_net_val: Double,
    val return_quantity: Int,
    val return_net_val: Double,
)