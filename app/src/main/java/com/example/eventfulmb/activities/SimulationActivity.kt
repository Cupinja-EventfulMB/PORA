package com.example.eventfulmb.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfulmb.databinding.ActivitySimulationBinding

class SimulationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimulationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimulationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back Btn
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}
