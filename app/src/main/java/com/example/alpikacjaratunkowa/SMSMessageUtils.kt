package com.example.alpikacjaratunkowa

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale
import kotlin.properties.Delegates

object SMSMessageUtils {
    fun getCity(lat: Double, long:Double, mainActivity: Context) : String{
        var cityName=""
        var geoCoder = Geocoder(mainActivity, Locale.getDefault())
        var address = geoCoder.getFromLocation(lat,long,1)

        var area = address?.get(0)?.getAddressLine(0).toString()
        var city = address?.get(0)?.locality
        var state = address?.get(0)?.adminArea
        var postalCode = address?.get(0)?.postalCode
        var knownName = address?.get(0)?.featureName

        cityName = "$area"
        return cityName
    }
}