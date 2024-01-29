package com.example.alpikacjaratunkowa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat




class MainActivity : AppCompatActivity() {
    private lateinit var accelerometerValues: TextView
    private lateinit var gyroscopeValues: TextView
    private lateinit var gpsValues: TextView
    private lateinit var emergencyAlertManager: EmergencyAlertManager
    private lateinit var startEmergencyButton: Button
    private lateinit var settingsButton: Button

//    fun startMyService(){
//        startService(Intent(this, MyServices::class.java))
//    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, MyServices::class.java))

        accelerometerValues = findViewById(R.id.accelerometerValues)
        gyroscopeValues = findViewById(R.id.gyroscopeValues)
        gpsValues = findViewById(R.id.gpsValues)

        settingsButton = findViewById(R.id.settingsButton)

        settingsButton.setOnClickListener {
            openSettingsActivity()
        }

//        startEmergencyButton = findViewById(R.id.startEmergencyButton)
//        startMyService()
//        startEmergencyButton.setOnClickListener {
//            startMyService()
//
//        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.SEND_SMS),
                2
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
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.VIBRATE),
                3
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

    private fun openSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}