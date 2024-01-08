package com.example.eventfulmb.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfulmb.databinding.ActivityDistanceBinding

class DistanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDistanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDistanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

    }
}