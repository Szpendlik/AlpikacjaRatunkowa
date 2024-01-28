package com.example.alpikacjaratunkowa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity(){
    private lateinit var accelerometerValues: TextView
    private lateinit var gyroscopeValues: TextView
    private lateinit var gpsValues: TextView
    private lateinit var emergencyAlertManager: EmergencyAlertManager
    private lateinit var startEmergencyButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, MyServices::class.java))

        accelerometerValues = findViewById(R.id.accelerometerValues)
        gyroscopeValues = findViewById(R.id.gyroscopeValues)
        gpsValues = findViewById(R.id.gpsValues)


        // Obsługa kliknięcia przycisku
        startEmergencyButton.setOnClickListener {
            // Rozpocznij alert w przypadku kliknięcia przycisku
            emergencyAlertManager.startEmergencyAlert(10000, "790326216")
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("NOHOMO", "NOHOMO")
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(

                    Manifest.permission.SEND_SMS
                ), 2
            )
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("NOHOMO", "NOHOMO")
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(

                    Manifest.permission.VIBRATE
                ), 3
            )
        }




        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("NOHOMO", "NOHOMO")
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

                ),
                1
            )
            return
        }
    }
}