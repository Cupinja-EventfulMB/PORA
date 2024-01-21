package com.example.eventfulmb.module

import com.example.eventfulmb.R
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.MapView
import android.view.MotionEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import androidx.core.content.ContextCompat

class MapTapOverlay(private val onMapTapListener: OnMapTapListener) : Overlay() {
    private var lastMarker: Marker? = null

    interface OnMapTapListener {
        fun onMapTapped(geoPoint: GeoPoint)
    }

    override fun onSingleTapConfirmed(e: MotionEvent, mapView: MapView): Boolean {
        val projection = mapView.projection
        val geoPoint = projection.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint

        onMapTapListener.onMapTapped(geoPoint)

        lastMarker?.let {
            mapView.overlays.remove(it)
        }

        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        val drawable = ContextCompat.getDrawable(mapView.context, R.drawable.marker)
        marker.icon = drawable
        marker.title = "Tapped Location"

        mapView.overlays.add(marker)

        lastMarker = marker

        mapView.invalidate()

        return true
    }

    fun setLastMarker(marker: Marker, mapView: MapView) {
        lastMarker?.let {
            mapView.overlays.remove(it)
        }

        mapView.overlays.add(marker)
        lastMarker = marker
        mapView.invalidate()
    }

    fun getLastMarker(): Marker? {
        return lastMarker;
    }

}




