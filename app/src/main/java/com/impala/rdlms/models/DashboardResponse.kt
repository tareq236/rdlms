package com.impala.rdlms.models

data class DashboardResponse(
    val success: Boolean,
    val error: String,
    val message: String,
    val result: List<DashboardResult>
)

data class DashboardResult(
    val delivery_remaining: Int,
    val delivery_done: Int,
    val cash_remaining: Int,
    val cash_done: Int,
    val sap_id: Int,
    val total_gate_pass_amount: Double?,
    val total_collection_amount: Double?,
    val total_return_amount: Double?,
    val total_return_quantity: Int?,
    val due_amount_total: Double?,
    val previous_day_due: Int
)
