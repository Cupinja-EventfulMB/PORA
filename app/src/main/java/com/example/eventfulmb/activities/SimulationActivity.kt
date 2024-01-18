package com.example.eventfulmb.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfulmb.databinding.ActivitySimulationBinding

class SimulationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimulationBinding

    // todo connect with adapeter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimulationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back Btn
        binding.backBtn.setOnClickListener {
            finish()
        }
        // FAB Btn
        binding.fab.setOnClickListener{
            val intent = Intent(this, FABSimulationActivity::class.java)
            startActivity(intent)
        }
    }
}
