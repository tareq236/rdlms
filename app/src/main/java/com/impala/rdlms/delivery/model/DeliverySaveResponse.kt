package com.impala.rdlms.delivery.model

data class DeliverySaveResponse(
    val success: Boolean,
    val error: String,
    val message: String,
)
