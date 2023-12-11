package com.impala.rdlms.auth.models

data class RegistrationRequest(
    var sap_id: String,
    var full_name: String,
    var mobile_number: String,
    var user_type: String,
    var password: String
)