package com.example.eventfulmb.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventfulmb.R
import com.example.eventfulmb.databinding.ActivitySettingsBinding

class SettingsActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.saveButton.setOnClickListener{
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()

        }

        // Back Btn
        binding.backBtn.setOnClickListener {
            finish()
        }
    }


}