package com.impala.rdlms.auth.models

data class RegistrationResponse(
    val success: Boolean,
    val result: UserResult
)

data class UserResult(
    val sap_id: Int,
    val full_name: String,
    val mobile_number: String,
    val user_type: String,
    val password: String
)