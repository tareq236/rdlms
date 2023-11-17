package com.impala.rdlms.models

data class LoginResponse(
    val success: Boolean,
    val result: Result,
    val error: String,
    val message: String,
    val is_start_work: Boolean
)


data class Result(
    val sap_id: Int,
    val full_name: String,
    val mobile_number: String,
    val user_type: String,
    val status: Int,
    val created_at: String,
    val updated_at: String?
)

