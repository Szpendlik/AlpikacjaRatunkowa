package com.example.alpikacjaratunkowa

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AlertScreenActivity : AppCompatActivity() {
    private lateinit var callEmergencyNumberButton: Button
    private lateinit var callFriendNumberButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_screen)

        callEmergencyNumberButton = findViewById<Button>(R.id.callEmergencyNumber)

        callEmergencyNumberButton.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:1232456789")
            startActivity(callIntent)
        }
        callFriendNumberButton = findViewById<Button>(R.id.callFriendNumber)

        callFriendNumberButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val phoneNumber = sharedPreferences.getString("phoneNumber", "888119218") ?: "888119218"
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:${phoneNumber}")
            startActivity(callIntent)
        }

        val emergencyAlert = EmergencyAlertManager(this)
        Log.d("Alert","Alert Activity onCreate")
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val phoneNumber = sharedPreferences.getString("phoneNumber", "888119218") ?: "888119218"
        val alertDuration = sharedPreferences.getString("alertDuration", "10000")?.toLong() ?: 10000
        val location = sharedPreferences.getString("lastSeenLocation", null) ?: null
        emergencyAlert.startEmergencyAlert(alertDuration, phoneNumber, null, location)

    }
}
