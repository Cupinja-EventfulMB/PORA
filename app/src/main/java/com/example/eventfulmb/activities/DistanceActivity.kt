package com.example.eventfulmb.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventfulmb.MyApplication
import com.example.eventfulmb.adapter.AlertAdapter
import com.example.eventfulmb.databinding.ActivityDistanceBinding
import com.example.eventfulmb.module.Alert

class DistanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDistanceBinding
    private lateinit var app: MyApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDistanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication

        binding.backBtn.setOnClickListener {
            finish()
        }

        // Create sample data
        val alertItems = mutableListOf(
            Alert("SNG", "Alert me if more than 100 people are present"),
            Alert("Lutkovno", "Alert me if more than 100 people are present")
        )

        Log.d("DistanceActivity", "Number of items in alertItems: ${alertItems.size}")

        val adapter = AlertAdapter(alertItems, app)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.updateDataFromJson(this)



    }
}