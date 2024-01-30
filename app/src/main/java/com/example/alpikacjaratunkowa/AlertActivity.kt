package com.example.alpikacjaratunkowa

import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class AlertActivity : AppCompatActivity() {

    private lateinit var emergencyAlert: EmergencyAlertManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emergencyAlert = EmergencyAlertManager(this)
        Log.d("Alert","Alert Activity onCreate")
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val phoneNumber = sharedPreferences.getString("phoneNumber", "888119218") ?: "888119218"
        val alertDuration = sharedPreferences.getString("alertDuration", "10000")?.toLong() ?: 10000
        val location = sharedPreferences.getString("lastSeenLocation", null) ?: null
        val intent = Intent(this, AlertScreenActivity::class.java)
        startActivity(intent)
//        CoroutineScope(Dispatchers.Default).launch {
//            delay(1000) // Set the interval in milliseconds
            emergencyAlert.startEmergencyAlert(alertDuration, phoneNumber, null, location)
//        }
    }
}