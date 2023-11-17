package com.impala.rdlms.attendance

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.widget.ImageView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.impala.rdlms.R
import com.impala.rdlms.attendance.model.AttendanceResponse
import com.impala.rdlms.utils.ApiService
import com.impala.rdlms.utils.SessionManager
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class EveningAttendanceActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private val REQUEST_IMAGE_CAPTURE = 1
    private val locationPermissionCode = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var imageFilePath: String? = null
    private lateinit var captureImageButton: Button
    private lateinit var retakeImageButton: Button
    private lateinit var startWorkButton: Button
    private lateinit var imageView: ImageView
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evening_attendance)

        captureImageButton = findViewById(R.id.captureImageButton)
        retakeImageButton = findViewById(R.id.retakeImageButton)
        startWorkButton = findViewById(R.id.startWorkButton)
        imageView = findViewById(R.id.imageView)

        // Initialize the loading dialog
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Loading")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Check and request camera permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        }

        // Check and request location permission
        if (!checkLocationPermission()) {
            requestLocationPermission()
        }

        captureImageButton.setOnClickListener {
            captureImage()
        }

        retakeImageButton.setOnClickListener {
            retakeImage()
        }

        startWorkButton.setOnClickListener {
            compressAndUploadImage()
        }

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val imageFile = createImageFile()
            imageFilePath = imageFile.absolutePath
            val imageUri =
                FileProvider.getUriForFile(this, "com.impala.rclsfa.provider", imageFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, imageFileName)
    }

    private fun retakeImage() {
        imageFilePath = null
        imageView.setImageBitmap(null)
        captureImage()
    }

    private fun compressAndUploadImage() {
        if (imageFilePath != null) {
            runBlocking {
                try {
                    showLoadingDialog()
                    val compressedImageFile = compressImage(imageFilePath!!)
                    getCurrentLocation(object : OnSuccessListener<Location> {
                        override fun onSuccess(location: Location?) {
                            if (location != null) {
                                val latitude = location.latitude
                                val longitude = location.longitude
                                val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                                val imageRequestBody = RequestBody.create(MediaType.parse("image/*"), compressedImageFile)
                                val imagePart = MultipartBody.Part.createFormData("image", compressedImageFile.name, imageRequestBody)
                                val latitudeRequestBody = RequestBody.create(MediaType.parse("text/plain"), latitude.toString())
                                val longitudeRequestBody = RequestBody.create(MediaType.parse("text/plain"), longitude.toString())
                                val srIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), sharedPreferences.getString("id", ""))

                                val apiService = ApiService.CreateApi1()
                                val call = apiService.saveEveningAttendanceWithImage(srIdRequestBody, latitudeRequestBody, longitudeRequestBody, imagePart)

                                call.enqueue(object : Callback<AttendanceResponse> {
                                    override fun onResponse(call: Call<AttendanceResponse>, response: Response<AttendanceResponse>) {
                                        if (response.isSuccessful) {
                                            val apiResponse = response.body()
                                            if (apiResponse != null) {
                                                if(apiResponse.success){
                                                    dismissLoadingDialog()
                                                    showDialogBox(SweetAlertDialog.SUCCESS_TYPE, "SUCCESS-S5803", "Evening Attendance Submit Successfully")
                                                }else{
                                                    dismissLoadingDialog()
                                                    showDialogBox(SweetAlertDialog.WARNING_TYPE, "Waring-SF5803", apiResponse.message)
                                                }
                                            }else{
                                                dismissLoadingDialog()
                                                showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error-RN5803", "Response NULL value. Try later")
                                            }
                                        } else {
                                            dismissLoadingDialog()
                                            showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error-RR5803", "Response failed. Try later")
                                        }
                                    }

                                    override fun onFailure(call: Call<AttendanceResponse>, t: Throwable) {
                                        dismissLoadingDialog()
                                        showDialogBox(SweetAlertDialog.ERROR_TYPE, "Error-NF5803", "Network error")
                                    }
                                })
                                showToast("Image compressed and uploaded.")
                            } else {
                                dismissLoadingDialog()
                                showToast("Location data not available.")
                            }
                        }
                    })
                } catch (e: Exception) {
                    dismissLoadingDialog()
                    e.printStackTrace()
                    // Handle compression error
                    showToast("Image compression failed.")
                }
            }
        } else {
            showToast("Please capture an image.")
        }
    }

    private fun getCurrentLocation(callback: OnSuccessListener<Location>) {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(callback)
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Handle the case where permission is not granted
            showToast("Location permission not granted.")
        }
    }

    private suspend fun compressImage(imagePath: String): File {
        try {
            val imageFile = File(imagePath)
            val compressedImageFile = Compressor.compress(this, imageFile) {
                resolution(800, 600)
                quality(30) // Adjust image quality (0 to 100) as needed
                format(Bitmap.CompressFormat.PNG)
            }
            return compressedImageFile
        } catch (e: Exception) {
            e.printStackTrace()
            return File(imagePath) // Return the original image if compression fails
        }
    }

    private fun showToast(message: String) {
        // Implement showToast method to display messages
    }

    // Handle the result of the image capture
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageFilePath?.let { path ->
                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    FileProvider.getUriForFile(this, "com.impala.rclsfa.provider", File(path))
                )
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    // Handle the Up button click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed() // Navigate back to the previous activity
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationPermissionCode
        )
    }

    private fun showDialogBox(type: Int, title: String, message: String, callback: (() -> Unit)? = null) {
        val sweetAlertDialog = SweetAlertDialog(this, type)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmClickListener {
                it.dismissWithAnimation()
                callback?.invoke()

                finish()
            }
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
