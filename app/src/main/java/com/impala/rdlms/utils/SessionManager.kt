package com.impala.rdlms.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)

    var userId: Int?
        get() = sharedPreferences.getInt("user_id", 0)
        set(userId) = sharedPreferences.edit().putInt("user_id", userId!!).apply()

    var fullName: String?
        get() = sharedPreferences.getString("full_name", "")
        set(fullName) = sharedPreferences.edit().putString("full_name", fullName!!).apply()

    var mobileNumber: String?
        get() = sharedPreferences.getString("mobile_number", "")
        set(mobileNumber) = sharedPreferences.edit().putString("mobile_number", mobileNumber!!).apply()

    var isStartWork: Boolean?
        get() = sharedPreferences.getBoolean("is_start_work", false)
        set(isStartWork) = sharedPreferences.edit().putBoolean("is_start_work", isStartWork!!).apply()

    var deliveryType: String?
        get() = sharedPreferences.getString("delivery_type", "")
        set(deliveryType) = sharedPreferences.edit().putString("delivery_type", deliveryType!!).apply()
    var cashCollectionType: String?
        get() = sharedPreferences.getString("cash_collection_type", "")
        set(cashCollectionType) = sharedPreferences.edit().putString("cash_collection_type", cashCollectionType!!).apply()

}
