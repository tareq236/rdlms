package com.impala.rdlms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.impala.rdlms.auth.LoginActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // Check if the user is already logged in
            if (isUserLoggedIn()) {
                // User is already logged in, navigate to MainActivity
                val intent = Intent(this@SplashActivity, PermissionActivity::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, navigate to LoginActivity
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }

    private fun isUserLoggedIn(): Boolean {
        // Check if the user is logged in based on SharedPreferences
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        return isLoggedIn
    }
}
