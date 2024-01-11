package com.example.eventfulmb.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.eventfulmb.MyApplication
import com.example.eventfulmb.databinding.ActivityDisplayImageBinding
import com.example.eventfulmb.module.Image
import com.example.eventfulmb.module.Message
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DisplayImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayImageBinding
    private lateinit var app: MyApplication
    private lateinit var imageUri: Uri

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 42
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

        val imageView: ImageView = binding.imageView

        imageUri = intent.getParcelableExtra("imageUri", Uri::class.java)!!

        Glide.with(this)
            .load(imageUri)
            .into(imageView)

        binding.sendButton.setOnClickListener {
            saveImageWithLocation()
        }
    }

    @SuppressLint("Recycle")
    private fun readImageToByteArray(imageUri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(imageUri)
        return inputStream?.readBytes() ?: byteArrayOf()
    }


    private fun saveImageWithLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    val latitude = location?.latitude ?: 0.0
                    val longitude = location?.longitude ?: 0.0

                    val image = Image(
                        image = readImageToByteArray(imageUri),
                        time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        latitude = latitude,
                        longitude = longitude
                    )

                    val gson: Gson = GsonBuilder().create()
                    val jsonMessage: String = gson.toJson(image)


                    val topicToSubscribe = "send/image"
                    app.publishMessage(topicToSubscribe, jsonMessage)

                }
        } else {
            requestLocationPermission()
        }
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


}