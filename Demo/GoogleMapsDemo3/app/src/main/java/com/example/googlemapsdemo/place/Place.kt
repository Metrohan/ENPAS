package com.example.googlemapsdemo.place

import com.google.android.gms.maps.model.LatLng

data class Place(
    val name: String,
    val address: String,
    val emptyparkingSpace: Int,
    val totalparkingSpace: Int,
    val latLng: LatLng
)