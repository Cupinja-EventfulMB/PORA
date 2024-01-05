package com.example.eventfulmb.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eventfulmb.databinding.ActivityMessageBinding
import com.example.eventfulmb.module.Message
import com.google.android.gms.location.*
import java.time.LocalDateTime

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding

    // Location variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionCode = 42 // You can choose any code
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.messageInput.setEndIconOnClickListener(View.OnClickListener {
            binding.messageInput.editText?.text = null
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()

        binding.saveButton.setOnClickListener {
            val messageBody = binding.messageInput.editText?.text.toString()
            val messageCategory = binding.menu.editText?.text.toString()

            if (messageBody.isNotEmpty()) {
                saveMessageWithLocation(messageBody, messageCategory)
            } else {
                Log.d("MessageActivity", "Message body is empty. Cannot save.")
            }
        }
    }


    private fun saveMessageWithLocation(messageBody: String, messageCategory: String) {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    val latitude = location?.latitude ?: 0.0
                    val longitude = location?.longitude ?: 0.0

                    val message = Message(
                        body = messageBody,
                        category = messageCategory,
                        time = LocalDateTime.now(),
                        latitude = latitude,
                        longitude = longitude
                    )

                    Log.d("MessageActivity", "Saved Message: $message")

                    binding.messageInput.editText?.text = null
                    binding.menu.editText?.text = null
                    binding.menu.clearFocus()
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