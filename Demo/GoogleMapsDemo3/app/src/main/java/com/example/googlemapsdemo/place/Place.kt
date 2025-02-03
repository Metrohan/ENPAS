package com.example.googlemapsdemo.place

import com.google.android.gms.maps.model.LatLng

data class Place(
    val name: String,
    val address: String,
    val rating: Float,
    val latLng: LatLng
)