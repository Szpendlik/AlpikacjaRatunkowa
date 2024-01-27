package com.example.alpikacjaratunkowa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import android.Manifest

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private lateinit var accelerometerValues: TextView
    private lateinit var gyroscopeValues: TextView
    private lateinit var emergencyAlertManager: EmergencyAlertManager
    private lateinit var startEmergencyButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accelerometerValues = findViewById(R.id.accelerometerValues)
        gyroscopeValues = findViewById(R.id.gyroscopeValues)
        startEmergencyButton = findViewById(R.id.startEmergencyButton)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        emergencyAlertManager = EmergencyAlertManager(this)

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            accelerometerValues.text = "No accelerometer found"
        }

        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            gyroscopeValues.text = "No gyroscope found"
        }

        // Obsługa kliknięcia przycisku
        startEmergencyButton.setOnClickListener {
            // Rozpocznij alert w przypadku kliknięcia przycisku
          //  emergencyAlertManager.startEmergencyAlert(10000, "")
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
                ),
                1
            )
            return
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val values = "X: $x\nY: $y\nZ: $z"
            accelerometerValues.text = "Accelerometer Values:\n$values"
        }
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val values = "X: $x\nY: $y\nZ: $z"
            gyroscopeValues.text = "Gyroscope Values:\n$values"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}
