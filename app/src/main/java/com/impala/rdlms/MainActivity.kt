package com.impala.rdlms

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
import com.impala.rdlms.cash_collection.CashCollectionActivity
import com.impala.rdlms.delivery.DeliveryActivity
import com.impala.rdlms.models.DashboardResponse
import com.impala.rdlms.models.DashboardResult
import com.impala.rdlms.utils.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
            sessionManager.deliveryType = "Remaining"
            val intent = Intent(this@MainActivity, DeliveryActivity::class.java)
            startActivity(intent)
        }
        mcvDeliveryDone.setOnClickListener {
            sessionManager.deliveryType = "Done"
            val intent = Intent(this@MainActivity, DeliveryActivity::class.java)
            startActivity(intent)
        }
        mcvCashCollectionRemaining.setOnClickListener {
            sessionManager.deliveryType = "Remaining"
            val intent = Intent(this@MainActivity, CashCollectionActivity::class.java)
            startActivity(intent)
        }
        mcvCashCollectionDone.setOnClickListener {
            sessionManager.deliveryType = "Done"
            val intent = Intent(this@MainActivity, CashCollectionActivity::class.java)
            startActivity(intent)
        }

        val apiService = ApiService.CreateApi1()
        val userId = sessionManager.userId
        showLoadingDialog()
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
