package com.example.googlemapsdemo

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.googlemapsdemo.place.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        map.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
        getLastLocation()
        addMarkers()
    }

    private fun getLastLocation() {
        // 📌 Konum izni kontrolü
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            // 📌 Son bilinen konumu al
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                }
            }
        } else {
            // 📌 İzin isteme
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getLastLocation()
            }
        }

    private fun addMarkers() {
        val places = listOf(
            Place("Vadistanbul", "Azerbaycan Cad.", 4, 4,LatLng(41.107556, 28.988431)),
            Place("AXİS AVM", "Kağıthane", 3, 5,LatLng(41.086367, 28.982114))
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