package com.impala.rdlms.attendance

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.impala.rdlms.R
import com.impala.rdlms.databinding.ActivityAttendanceMenuBinding

class AttendanceMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttendanceMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    fun onMorningAttendanceButtonClick(view: View?) {
        val intent = Intent(this, MorningAttendanceActivity::class.java)
        startActivity(intent)
    }

    fun onEveningAttendanceButtonClick(view: View?) {
        val intent = Intent(this, EveningAttendanceActivity::class.java)
        startActivity(intent)
    }

    // Handle the Up button click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed() // Navigate back to the previous activity
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}
