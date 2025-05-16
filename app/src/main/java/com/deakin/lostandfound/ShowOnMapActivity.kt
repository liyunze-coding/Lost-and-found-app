package com.deakin.lostandfound

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.deakin.lostandfound.data.DatabaseHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ShowOnMapActivity : AppCompatActivity(), OnMapReadyCallback  {
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_on_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val db = DatabaseHelper(this, null)

        val items = db.getItems()

        for (item in items) {
            val latlng = LatLng(item.latitude, item.longitude)
            googleMap.addMarker(MarkerOptions().position(latlng).title(item.location))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14f))
        }
    }

}