package com.example.googlemapsdemo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.example.googlemapsdemo.place.Place

class MarkerInfoWindowAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {

    @SuppressLint("MissingInflatedId")
    override fun getInfoContents(marker: Marker): View? {
        val place = marker.tag as? Place ?: return null

        val view = LayoutInflater.from(context).inflate(R.layout.marker_info_contents, null)
        view.findViewById<TextView>(R.id.text_view_title)?.text = place.name
        view.findViewById<TextView>(R.id.text_view_address)?.text = place.address
        view.findViewById<TextView>(R.id.text_view_emptyParkingSpace)?.text = "Müsait Park Alanı: %s".format(place.emptyparkingSpace)
        view.findViewById<TextView>(R.id.text_view_totalParkingSpace)?.text = "Toplam Park Alanı: %s".format(place.totalparkingSpace)
        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}
