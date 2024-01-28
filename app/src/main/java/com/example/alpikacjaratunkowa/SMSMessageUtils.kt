package com.example.alpikacjaratunkowa

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationCallback
import java.util.Locale

object SMSMessageUtils {
    fun getCity(lat: Double, long:Double, mainActivity: Context) : String{
        var cityName=""
        var geoCoder = Geocoder(mainActivity, Locale.getDefault())
//        if (android.os.Build.VERSION.SDK_INT >= 33) {
//            var address = geoCoder.getFromLocation(lat, long, 1, Geocoder.GeocodeListener { value ->
//            })
//        } else {
        var address = geoCoder.getFromLocation(lat,long,1)
//        }

        var area = address?.get(0)?.getAddressLine(0).toString()
        var city = address?.get(0)?.locality
        var state = address?.get(0)?.adminArea
        var postalCode = address?.get(0)?.postalCode
        var knownName = address?.get(0)?.featureName

        cityName = "$area,$city,$state,$postalCode,$knownName"
        return cityName
    }
}