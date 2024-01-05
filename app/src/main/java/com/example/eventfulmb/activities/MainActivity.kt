package com.example.eventfulmb.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eventfulmb.R
import com.example.eventfulmb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.simulationActivityButton.setOnClickListener {
            val simulationIntent= Intent(this,SimulationActivity::class.java)
            startActivity(simulationIntent)
        }

        binding.settingsActivityButton.setOnClickListener {
            val settingsIntent= Intent(this,SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
        binding.fabCamera.setOnClickListener{
            Toast.makeText(this, "Camera button clicked!", Toast.LENGTH_SHORT).show()

        }

        // Exit application
        val exitButton = binding.exitBtn
        exitButton.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Do you want to exit the application?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes"){ _,_ -> finish()}
                    .create().show()
        }
    }


}