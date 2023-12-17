package com.impala.rdlms.utils

import com.impala.rdlms.attendance.model.AttendanceResponse
import com.impala.rdlms.auth.models.RegistrationRequest
import com.impala.rdlms.auth.models.RegistrationResponse
import com.impala.rdlms.cash_collection.model.CashCollectionSave
import com.impala.rdlms.delivery.model.DeliveryResponse
import com.impala.rdlms.delivery.model.DeliverySave
import com.impala.rdlms.delivery.model.DeliverySaveResponse
import com.impala.rdlms.models.DashboardResponse
import com.impala.rdlms.models.LoginRequest
import com.impala.rdlms.models.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @PUT("api/v1/cash_collection/save/{id}")
    fun cashCollection(@Body request: CashCollectionSave, @Path("id") id:Int): Call<DeliverySaveResponse>

    @GET("api/v1/cash_collection/v2/list/{sap_id}")
    fun getCashCollectionRemainingList(@Path("sap_id") userId: String, @Query("type") type: String, ): Call<DeliveryResponse>

    @POST("api/v1/delivery/save")
    fun saveDeliveryData(@Body request: DeliverySave): Call<DeliverySaveResponse>

    @GET("api/v1/delivery/v2/list/{sap_id}")
    fun getDeliveryRemainingList(@Path("sap_id") userId: String, @Query("type") type: String, ): Call<DeliveryResponse>

    @GET("api/v1/reports/dashboard/{sap_id}")
    fun getDashboardDetails(@Path("sap_id") userId: String, ): Call<DashboardResponse>

    @Multipart
    @POST("api/save_end_attendance_sr_with_image")
    fun saveEveningAttendanceWithImage(
        @Part("sr_id") sr_id: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<AttendanceResponse>

    @Multipart
    @POST("api/v1/attendance/start_work")
    fun saveMorningAttendanceWithImage(
        @Part("sap_id") sap_id: RequestBody,
        @Part("start_latitude") latitude: RequestBody,
        @Part("start_longitude") longitude: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<AttendanceResponse>

    @GET("api/v1/user_details")
    fun getUserDetails(@Query("sap_id") userId: String): Call<LoginResponse>

    @POST("api/v1/user_login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/v1/user_registration")
    fun userRegistration(@Body request: RegistrationRequest): Call<RegistrationResponse>

    companion object {
        // Python Django This function creates an instance of ApiService
        fun CreateApi1(): ApiService {
            // Set up Retrofit and return an instance of ApiService
            val retrofit = Retrofit.Builder()
                .baseUrl("http://116.68.200.97:6042")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

}

