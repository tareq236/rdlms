package com.impala.rdlms.db

data class ProductModel(
    val id: String?,
    val prodName: String,
    val quantity: String,
    val tp: String,
    val vat: String,
    val receivedQty: String,
    val receivedAmount: String,
    val invoiceId:String
    )