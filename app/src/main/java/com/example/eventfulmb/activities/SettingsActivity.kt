package com.example.eventfulmb.activities

import android.os.Bundle
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

        val hourSlider = binding.sliderHour
        val minuteSlider = binding.sliderMinutes
        val secondSlider = binding.sliderSeconds

        val hourTextView = binding.txtViewH
        val minuteTextView = binding.txtViewM
        val secondTextView = binding.txtViewS

        hourSlider.addOnChangeListener{ slider, value, fromUser ->
            hourTextView.text = value.toInt().toString()
        }

        minuteSlider.addOnChangeListener{ slider, value, fromUser ->
            minuteTextView.text = value.toInt().toString()
        }

        secondSlider.addOnChangeListener{ slider, value, fromUser ->
            secondTextView.text = value.toInt().toString()
        }

        binding.saveButton.setOnClickListener {
            val isInvalidValues = hourTextView.text == "Hours" || minuteTextView.text == "Minutes" || secondTextView.text == "Seconds"

            if (isInvalidValues) {
                Toast.makeText(this, "Change all values", Toast.LENGTH_SHORT).show()
            } else  {
                val selectedHour = hourSlider.value.toInt()
                val selectedMinute = minuteSlider.value.toInt()
                val selectedSecond = secondSlider.value.toInt()
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            }
        }

        // Back Btn
        binding.backBtn.setOnClickListener {
            finish()
        }
    }


}