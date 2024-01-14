package com.example.eventfulmb.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eventfulmb.MyApplication
import com.example.eventfulmb.R
import com.example.eventfulmb.databinding.ActivityMainBinding
import com.example.eventfulmb.module.MqttHandler

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var app: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication

        val topicToSubscribe = "people/detection"
        app.subscribeToTopic(topicToSubscribe)

        binding.simulationActivityButton.setOnClickListener {
            val simulationIntent= Intent(this,SimulationActivity::class.java)
            startActivity(simulationIntent)
        }

        binding.settingsActivityButton.setOnClickListener {
            val settingsIntent= Intent(this,SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
        binding.fabCamera.setOnClickListener{
            val cameraIntent = Intent(this,CameraActivity::class.java)
            Toast.makeText(this, "Camera button clicked!", Toast.LENGTH_SHORT).show()
            startActivity(cameraIntent)
        }

        binding.messageButton.setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
            startActivity(intent)
        }

        binding.distanceButton.setOnClickListener {
            val intent = Intent(this, DistanceActivity::class.java)
            startActivity(intent)
        }

        val exitButton = binding.exitBtn
        exitButton.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Do you want to exit the application?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes"){ _,_ -> finish()}
                    .create().show()
        }
    }

    override fun onDestroy() {
        (application as MyApplication).disconnectMqtt()
        super.onDestroy()
    }

}