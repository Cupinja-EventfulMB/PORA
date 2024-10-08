package com.example.eventfulmb.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventfulmb.MyApplication
import com.example.eventfulmb.adapter.RecyclerAdapter
import com.example.eventfulmb.databinding.ActivitySimulationBinding


class SimulationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimulationBinding
    private lateinit var app: MyApplication
    private lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimulationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication
        val sensorDataList = app.sensorData

        adapter = RecyclerAdapter(sensorDataList) { sensorData, position ->
            adapter.updateItem(sensorData, position)
            app.saveSensorDataToFile()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SimulationActivity)
            adapter = this@SimulationActivity.adapter
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, FABSimulationActivity::class.java)
            startActivity(intent)
        }
    }
}


