package com.liao.liaofusedapidemo

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    var mapFragment: SupportMapFragment? = null
    var mMap: GoogleMap? = null
    var marker: Marker? = null
    var receiver: LocationBroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_map_activity)
        receiver = LocationBroadcastReceiver()
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                //Req Location Permission
                startLocService()
            }
        } else {
            //Start the Location Service
            startLocService()
        }
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapFrag) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    fun startLocService() {
        val filter = IntentFilter("ACT_LOC")
        registerReceiver(receiver, filter)
        val intent = Intent(this@MainActivity, LocationService::class.java)
        startService(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //startLocService();
            } else {
                Toast.makeText(this, "Give me permissions", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    inner class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "ACT_LOC") {
                val lat = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                if (mMap != null) {
                    val latLng = LatLng(lat, longitude)
                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLng)
                    if (marker != null) marker!!.position = latLng else marker = mMap!!.addMarker(markerOptions)
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F))
                }
                Toast.makeText(this@MainActivity, "Latitude is: $lat, Longitude is $longitude", Toast.LENGTH_LONG).show()
            }
        }
    }
}