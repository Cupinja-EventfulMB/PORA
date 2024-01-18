package com.example.eventfulmb.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.eventfulmb.R
import com.example.eventfulmb.databinding.ActivityFabsimulationBinding
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class FABSimulationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFabsimulationBinding
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding before setting the content view
        binding = ActivityFabsimulationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setUseDataConnection(true)
        mapController = map.controller
        // Center the map
        val mariborCoordinates = GeoPoint(46.5547, 15.6459)
        mapController.setCenter(mariborCoordinates)
        mapController.setZoom(15.0)


        // binding.mapText. todo

        val hourSlider = binding.sliderHour
        val minuteSlider = binding.sliderMinutes
        val secondSlider = binding.sliderSeconds

        val hourTextView = binding.txtViewH
        val minuteTextView = binding.txtViewM
        val secondTextView = binding.txtViewS

        hourSlider.addOnChangeListener { slider, value, fromUser ->
            hourTextView.text = value.toInt().toString()
        }

        minuteSlider.addOnChangeListener { slider, value, fromUser ->
            minuteTextView.text = value.toInt().toString()
        }

        secondSlider.addOnChangeListener { slider, value, fromUser ->
            secondTextView.text = value.toInt().toString()
        }

        binding.saveButton.setOnClickListener {
            val isInvalidValues =
                hourTextView.text == "Hours" || minuteTextView.text == "Minutes" || secondTextView.text == "Seconds"

            if (isInvalidValues) {
                Toast.makeText(this, "Change all values", Toast.LENGTH_SHORT).show()
            } else {
                val selectedHour = hourSlider.value.toInt()
                val selectedMinute = minuteSlider.value.toInt()
                val selectedSecond = secondSlider.value.toInt()
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            }
        }


    }
}