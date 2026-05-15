package com.example.rakta_seva

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.util.Locale

object LocationUtils {

    fun getCurrentLocationName(context: Context, callback: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback("Location Permission Denied")
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (location != null) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val cityName = address.locality ?: address.subAdminArea ?: "Unknown City"
                    val stateName = address.adminArea ?: ""
                    callback("$cityName, $stateName")
                } else {
                    callback("Tumakuru, Karnataka (Simulated)")
                }
            } catch (e: Exception) {
                callback("Bangalore, Karnataka (Simulated)")
            }
        } else {
            callback("Mysuru, Karnataka (Simulated)")
        }
    }
}