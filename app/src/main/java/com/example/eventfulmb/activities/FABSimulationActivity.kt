package com.example.eventfulmb.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.io.IOException
import android.content.Context
import android.view.inputmethod.InputMethodManager
import org.json.JSONObject
import com.example.eventfulmb.module.SensorData
import android.os.Environment
import com.example.eventfulmb.MyApplication
import java.io.File
import java.io.FileWriter

class FABSimulationActivity : AppCompatActivity(), MapTapOverlay.OnMapTapListener {
    private lateinit var binding: ActivityFabsimulationBinding
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private lateinit var locationSearchView: AutoCompleteTextView
    private lateinit var geocoder: Geocoder
    private lateinit var locationMarker: Marker
    private lateinit var tapOverlay: MapTapOverlay
    private lateinit var app: MyApplication
    private var sensorDataList = mutableListOf<SensorData>()

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFabsimulationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MyApplication
        sensorDataList = app.sensorData

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
            val selectedCategory = categoryMenu?.text.toString()
            val selectedHour = hourSlider.value.toInt()
            val selectedMinute = minuteSlider.value.toInt()
            val selectedSecond = secondSlider.value.toInt()
            val locationName = locationSearchView.text.toString()

            val minValueField = binding.insertMinVal.text.toString()
            val minValue = minValueField.toIntOrNull() ?: 0

            val maxValueField = binding.insertMaxVal.text.toString()
            val maxValue = maxValueField.toIntOrNull() ?: 0

            val lastMarkerPosition = tapOverlay.getLastMarker()?.position
            val latitude = lastMarkerPosition?.latitude ?: 0.0
            val longitude = lastMarkerPosition?.longitude ?: 0.0

            val subscribed = binding.switch1.isChecked

            if (selectedCategory.isNotBlank() && minValueField.isNotBlank() && maxValueField.isNotBlank() && locationName.isNotBlank() && lastMarkerPosition != null) {
                val sensorData = SensorData(
                    category = selectedCategory,
                    minVal = minValue,
                    maxVal = maxValue,
                    hour = selectedHour,
                    minutes = selectedMinute,
                    seconds = selectedSecond,
                    location = locationName,
                    latitude = latitude,
                    longitude = longitude,
                    subscribed = subscribed
                )

                sensorDataList.add(sensorData)
                saveSensorDataToJsonFile()
                Toast.makeText(this, "Data Saved: $sensorData", Toast.LENGTH_SHORT).show()
                Log.d("Saved sensor data", "$sensorData");
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun createMarkerAtLocation(geoPoint: GeoPoint, locationName: String? = null) {
        val marker = Marker(map)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        val drawable = ContextCompat.getDrawable(this@FABSimulationActivity, R.drawable.marker)
        marker.icon = drawable
        marker.title = locationName ?: getString(R.string.default_location)

        tapOverlay.setLastMarker(marker, map)
    }

    override fun onMapTapped(geoPoint: GeoPoint) {
        Log.i("FABSimulationActivity", "Map tapped at: Latitude: ${geoPoint.latitude}, Longitude: ${geoPoint.longitude}")

        createMarkerAtLocation(geoPoint)

        val addresses: MutableList<Address>? = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressFragments = with(address) {
                    (0..maxAddressLineIndex).map { getAddressLine(it) }
                }

                runOnUiThread {
                    locationSearchView.setText(addressFragments.joinToString(separator = "\n"))
                }
            } else {
                Log.w("FABSimulationActivity", "No address found at tapped location.")
            }
        }
    }

    private fun updateRangeText(selectedCategory: String) {
        val rangeText = when (selectedCategory) {
            "People" -> getString(R.string.people_range)
            "Temperature" -> getString(R.string.temperature_range)
            else -> getString(R.string.default_category)
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

                            createMarkerAtLocation(geoPoint, locationName)
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

    private fun saveSensorDataToJsonFile() {
        val jsonArray = JSONArray()
        sensorDataList.forEach { data ->
            val jsonObject = JSONObject()
            jsonObject.put("category", data.category)
            jsonObject.put("lastUpdate", data.lastUpdate)
            jsonObject.put("hour", data.hour)
            jsonObject.put("minutes", data.minutes)
            jsonObject.put("seconds", data.seconds)
            jsonObject.put("minVal", data.minVal)
            jsonObject.put("maxVal", data.maxVal)
            jsonObject.put("location", data.location)
            jsonObject.put("latitude", data.latitude)
            jsonObject.put("longitude", data.longitude)
            jsonObject.put("subscribed", data.subscribed)
            jsonArray.put(jsonObject)
        }

        try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val file = File(getExternalFilesDir(null), "SensorData.json")
                FileWriter(file).use {
                    it.write(jsonArray.toString(4)) // Use 4 for pretty print
                }
                Toast.makeText(this, "Data saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
                Log.d("Data saved", "${file.absolutePath}")
            } else {
                Toast.makeText(this, "Cannot write to external storage", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
        }
    }
}