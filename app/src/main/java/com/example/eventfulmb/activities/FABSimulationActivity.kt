package com.example.eventfulmb.activities

import android.annotation.SuppressLint
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
import com.example.eventfulmb.module.MapTapOverlay
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.location.Address
import android.location.Geocoder
import org.osmdroid.views.overlay.Marker
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import android.content.Context
import android.view.inputmethod.InputMethodManager

class FABSimulationActivity : AppCompatActivity(), MapTapOverlay.OnMapTapListener {
    private lateinit var binding: ActivityFabsimulationBinding
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private lateinit var locationSearchView: AutoCompleteTextView
    private lateinit var geocoder: Geocoder
    private lateinit var locationMarker: Marker
    private lateinit var tapOverlay: MapTapOverlay

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFabsimulationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setUseDataConnection(true)
        mapController = map.controller

        binding.txtInsertRange.text = getString(R.string.insert_range, getString(R.string.default_category))

        val mariborCoordinates = GeoPoint(46.5547, 15.6459)
        mapController.setCenter(mariborCoordinates)
        mapController.setZoom(15.0)

        tapOverlay = MapTapOverlay(this)
        map.overlays.add(tapOverlay)

        // binding.mapText. todo

        geocoder = Geocoder(this)

        locationSearchView = binding.mapText
        locationSearchView.setOnEditorActionListener { _, actionId, _ ->
            Log.d("FABSimulationActivity", "Editor action triggered")
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("FABSimulationActivity", "Search action identified")
                val locationName = locationSearchView.text.toString()
                Log.d("LocationSearch", locationName)
                searchLocation(locationName)
                true
            } else {
                Log.d("FABSimulationActivity", "Different action identified: $actionId")
                false
            }
        }


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

        val categoryMenu = binding.menu.editText as? AutoCompleteTextView
        categoryMenu?.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.categoriesSimulation)))

        categoryMenu?.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = categoryMenu.adapter.getItem(position) as String
            updateRangeText(selectedCategory)
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

    override fun onMapTapped(geoPoint: GeoPoint) {
        Log.i("FABSimulationActivity", "Map tapped at: Latitude: ${geoPoint.latitude}, Longitude: ${geoPoint.longitude}")

    }

    private fun updateRangeText(selectedCategory: String) {
        val rangeText = when (selectedCategory) {
            "People" -> getString(R.string.people_range)
            "Temperature" -> getString(R.string.temperature_range)
            else -> getString(R.string.default_category)  // default placeholder for category
        }
        binding.txtInsertRange.text = getString(R.string.insert_range, rangeText)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


    private fun searchLocation(locationName: String) {
        val url = "https://nominatim.openstreetmap.org/search?q=${locationName.replace(" ", "+")}&format=json"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@FABSimulationActivity, "Request Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                response.body?.close()

                if (responseData != null) {
                    val jsonArray = JSONArray(responseData)
                    if (jsonArray.length() > 0) {
                        val jsonObject = jsonArray.getJSONObject(0)
                        val lat = jsonObject.getDouble("lat")
                        val lng = jsonObject.getDouble("lon")

                        runOnUiThread {
                            hideKeyboard()
                            val geoPoint = GeoPoint(lat, lng)
                            mapController.setCenter(geoPoint)
                            mapController.setZoom(15.0)

                            val marker = Marker(map)
                            marker.position = geoPoint
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            val drawable = ContextCompat.getDrawable(this@FABSimulationActivity, R.drawable.marker)
                            marker.icon = drawable
                            marker.title = locationName

                            tapOverlay.setLastMarker(marker, map)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@FABSimulationActivity, "Location not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}