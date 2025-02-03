package com.example.googlemapsdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.googlemapsdemo.place.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        map.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))

        addMarkers()
    }


    private fun addMarkers() {
        val places = listOf(
            Place("Vadistanbul", "Azerbaycan Cad.", 4.5f, LatLng(41.107556, 28.988431)),
            Place("AXİS AVM", "Kağıthane", 4.2f, LatLng(41.086367, 28.982114))
        )

        googleMap?.let { map ->
            places.forEach { place ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .title(place.name)
                        .position(place.latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon2))
                )

                marker?.tag = place


                val latLng = LatLng(41.17, 29.056)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
            }
        }
    }
}