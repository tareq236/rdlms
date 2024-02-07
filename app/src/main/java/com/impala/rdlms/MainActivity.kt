package com.impala.rdlms

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.impala.rdlms.auth.LoginActivity
import com.impala.rdlms.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.os.Handler
import android.os.Looper
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.card.MaterialCardView
import com.impala.rdlms.auth.EditProfileActivity
import com.impala.rdlms.cash_collection.CashCollectionActivity
import com.impala.rdlms.delivery.DeliveryRemainingActivity
import com.impala.rdlms.models.DashboardResponse
import com.impala.rdlms.models.DashboardResult
import com.impala.rdlms.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.impala.rdlms.utils.LocationForegroundService
import com.impala.rdlms.utils.SocketManager
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var sessionManager: SessionManager
    private lateinit var loadingDialog: Dialog
    private lateinit var txvTime: TextView
    private lateinit var txvDate: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimeRunnable: Runnable
    private lateinit var dashboardResult: List<DashboardResult>

    // FOR LOCATION TRACKING
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        sessionManager = SessionManager(this)
        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading")

        val txvDeliveryRemaining: TextView = findViewById(R.id.txv_delivery_remaining)
        val txvDeliveryDone: TextView = findViewById(R.id.txv_delivery_done)
        val txvCashCollectionRemaining: TextView = findViewById(R.id.txv_cash_collection_remaining)
        val txvCashCollectionDone: TextView = findViewById(R.id.txv_cash_collection_done)

        val mcvDeliveryRemaining: MaterialCardView = findViewById(R.id.mcv_delivery_remaining)
        val mcvDeliveryDone: MaterialCardView = findViewById(R.id.mcv_delivery_done)
        val mcvCashCollectionRemaining: MaterialCardView = findViewById(R.id.mcv_cash_collection_remaining)
        val mcvCashCollectionDone: MaterialCardView = findViewById(R.id.mcv_cash_collection_done)
        val mcvReturnDone: MaterialCardView = findViewById(R.id.mcv_return_done)

        txvTime = findViewById(R.id.txv_time)
        txvDate = findViewById(R.id.txv_date)
        val txvUserName: TextView = findViewById(R.id.txv_user_name)
        txvUserName.text = sessionManager.fullName+"("+sessionManager.userId+")"

        // Initialize the update time Runnable
        updateTimeRunnable = object : Runnable {
            override fun run() {
                updateDateTime()
                handler.postDelayed(this, 1000) // Update every 1000 milliseconds (1 second)
            }
        }

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_menu_profile -> {
                    startActivity(Intent(this,EditProfileActivity::class.java))
                    true
                }
                R.id.nav_menu_notification -> {
                    // Handle Notification item click
                    // Add your code here

                    true
                }
                R.id.nav_menu_setting -> {
                    // Handle Setting item click
                    // Add your code here

                    true
                }
                R.id.nav_menu_logout -> {
                    val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                // Add more cases for other menu items if needed
                else -> false
            }
        }

        mcvDeliveryRemaining.setOnClickListener {
            sessionManager.deliveryType = "DeliveryRemaining"
            val intent = Intent(this@MainActivity, DeliveryRemainingActivity::class.java)
            startActivity(intent)
        }
        mcvDeliveryDone.setOnClickListener {
            sessionManager.deliveryType = "DeliveryDone"
            val intent = Intent(this@MainActivity, DeliveryRemainingActivity::class.java)
            startActivity(intent)
        }
        mcvCashCollectionRemaining.setOnClickListener {
            sessionManager.deliveryType = "CashRemaining"
            val intent = Intent(this@MainActivity, CashCollectionActivity::class.java)
            startActivity(intent)
        }
        mcvCashCollectionDone.setOnClickListener {
            sessionManager.deliveryType = "CashDone"
            val intent = Intent(this@MainActivity, CashCollectionActivity::class.java)
            startActivity(intent)
        }
        mcvReturnDone.setOnClickListener {
            sessionManager.deliveryType = "ReturnDone"
            val intent = Intent(this@MainActivity, CashCollectionActivity::class.java)
            startActivity(intent)
        }

        val serviceIntent = Intent(this, LocationForegroundService::class.java)
        startService(serviceIntent)

    // FOR LOCATION TRACKING
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up location request parameters
//        locationRequest = LocationRequest.create().apply {
//            interval = 5000 // 5 seconds
//            fastestInterval = 3000 // 3 seconds
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }

//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult?.lastLocation?.let { location ->
//                    // Handle the new location update
//                    updateLocationUI(location)
//                }
//            }
//        }

        // Check and request location permissions
//        if (ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            startLocationUpdates()
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION_PERMISSION
//            )
//        }
    // FOR LOCATION TRACKING

//        val apiService = ApiService.CreateApi1()
//        val userId = sessionManager.userId


    }

//    private fun startLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
//    }

    private fun updateLocationUI(location: Location) {
        Log.d("LocationTracking", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
        Log.d("LocationTracking", "Accuracy: ${location.accuracy} meters")

        // Update your UI with the new location data
        // For example, update a map, send the location to a server, etc.
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationUpdates()
//            } else {
//                // Handle the case where the user denied the location permission
//            }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop location updates when the activity is destroyed
//        fusedLocationClient.removeLocationUpdates(locationCallback)

    }

    private fun getDashboardDetails(){

        val txvDeliveryRemaining: TextView = findViewById(R.id.txv_delivery_remaining)
        val txvDeliveryDone: TextView = findViewById(R.id.txv_delivery_done)
        val txvCashCollectionRemaining: TextView = findViewById(R.id.txv_cash_collection_remaining)
        val txvCashCollectionDone: TextView = findViewById(R.id.txv_cash_collection_done)
        val txvReturnDone: TextView = findViewById(R.id.txv_return_done)


        val apiService = ApiService.CreateApi1()
        val userId = sessionManager.userId
        apiService.getDashboardDetails(userId.toString()).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful) {
                    val response = response.body()
                    if (response != null) {
                        if(response.success){
                            dismissLoadingDialog()
                            dashboardResult = response.result

                            txvDeliveryRemaining.text = dashboardResult[0].delivery_remaining.toString()
                            txvDeliveryDone.text = dashboardResult[0].delivery_done.toString()
                            txvCashCollectionRemaining.text = dashboardResult[0].cash_remaining.toString()
                            txvCashCollectionDone.text = dashboardResult[0].cash_done.toString()
//                            txvReturnDone.text = dashboardResult[0].total_return_quantity.toString();
                            if (dashboardResult[0].total_return_quantity != null) {
                                txvReturnDone.text = dashboardResult[0].total_return_quantity.toString();
                            } else {
                                txvReturnDone.text = "0";
                            }
                        }else{
                            dismissLoadingDialog()
                            showDialogBox(SweetAlertDialog.WARNING_TYPE, "Waring", response.message)
                        }
                    }else{
                        dismissLoadingDialog()
                        showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Response NULL value. Try later")
                    }
                }else{
                    dismissLoadingDialog()
                    showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Response failed. Try later")
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                dismissLoadingDialog()
                showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error", "Network error")
            }
        })
    }

    private fun updateDateTime() {
        val calendar = Calendar.getInstance()

        // Format time
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(calendar.time)

        // Format date
        val dateFormat = SimpleDateFormat("MMMM d yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        // Update TextViews
        txvTime.text = formattedTime
        txvDate.text = formattedDate
    }

    override fun onResume() {
        super.onResume()
        // Start updating time and date when the activity is resumed
        handler.post(updateTimeRunnable)
        showLoadingDialog()
        getDashboardDetails()
    }

    override fun onPause() {
        super.onPause()
        // Stop updating time and date when the activity is paused
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun showDialogBox(type: Int, title: String, message: String, callback: (() -> Unit)? = null) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
        sweetAlertDialog.show()
    }

    private fun showLoadingDialog() {
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

}
