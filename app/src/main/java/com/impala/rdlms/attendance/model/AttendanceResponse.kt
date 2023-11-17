package com.impala.rdlms.attendance.model

data class AttendanceResponse(
    val error: String,
    val message: String,
    val success: Boolean
)
