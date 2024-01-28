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


class MainActivity : AppCompatActivity(), SensorEventListener {

    private var thresholdAcc: Float = 0.5f
    private var thresholdGyro: Float = 0.5f
    private var lastAccX:Float = 0f
    private var lastAccY:Float = 0f
    private var lastAccZ:Float = 0f
    private var lastGyroX:Float = 0f
    private var lastGyroY:Float = 0f
    private var lastGyroZ:Float = 0f
    private var phoneNumber:String = "888119218"
    private var currentAccX:Float = 0f
    private var currentAccY:Float = 0f
    private var currentAccZ:Float = 0f
    private var currentGyroX:Float = 0f
    private var currentGyroY:Float = 0f
    private var currentGyroZ:Float = 0f



    private lateinit var sensorManager: SensorManager
    private val accelerometer: Sensor? = null
    private val gyroscope: Sensor? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var accelerometerValues: TextView
    private lateinit var gyroscopeValues: TextView
    private lateinit var gpsValues: TextView
    private lateinit var emergencyAlertManager: EmergencyAlertManager
    private lateinit var startEmergencyButton: Button

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this,MyServices::class.java))

        accelerometerValues = findViewById(R.id.accelerometerValues)
        gyroscopeValues = findViewById(R.id.gyroscopeValues)
        gpsValues = findViewById(R.id.gpsValues)
//        startEmergencyButton = findViewById(R.id.startEmergencyButton)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


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
//        startEmergencyButton.setOnClickListener {
            // Rozpocznij alert w przypadku kliknięcia przycisku
//            emergencyAlertManager.startEmergencyAlert(10000, "")
//        }
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
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("NOHOMO","NOHOMO")
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

                ),
                1
            )
            return

        } else {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (location in p0.locations){
                        print("Location UPdate")
                        gpsValues.text = SMSMessageUtils.getCity(location.latitude, location.longitude, this@MainActivity)
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        when(event.sensor.type){
            Sensor.TYPE_ACCELEROMETER -> {

                currentAccX = event.values[0]
                currentAccY = event.values[1]
                currentAccZ = event.values[2]

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                if (hasAccValueChanged(x, y, z)) {
                    val values = "X: $x\nY: $y\nZ: $z"
                    accelerometerValues.text = "Accelerometer Values:\n$values"
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                currentGyroX = event.values[0]
                currentGyroY = event.values[1]
                currentGyroZ = event.values[2]

                if (hasGyroValueChanged(x, y, z)){
                    val values = "X: $x\nY: $y\nZ: $z"
                    gyroscopeValues.text = "Gyroscope Values:\n$values"
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        SensorManager.SENSOR_STATUS_ACCURACY_HIGH
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun hasAccValueChanged(x:Float, y:Float, z:Float):Boolean{

        val deltaX = Math.abs(x - lastAccX)
        val deltaY = Math.abs(y - lastAccY)
        val deltaZ = Math.abs(z - lastAccZ)

        // Check if any of the differences is greater than the threshold
        if (deltaX > thresholdAcc || deltaY > thresholdAcc || deltaZ > thresholdAcc) {
            // Values have changed
            lastAccX = x
            lastAccY = y
            lastAccZ = z
            return true
        }

        // Values have not changed
        return false
    }
    private fun hasGyroValueChanged(x:Float, y:Float, z:Float):Boolean{

        val deltaX = Math.abs(x - lastGyroX)
        val deltaY = Math.abs(y - lastGyroY)
        val deltaZ = Math.abs(z - lastGyroZ)

        // Check if any of the differences is greater than the threshold
        if (deltaX > thresholdGyro || deltaY > thresholdGyro || deltaZ > thresholdGyro) {
            // Values have changed
            lastGyroX = x
            lastGyroY = y
            lastGyroZ = z
            return true
        }

        // Values have not changed
        return false
    }
    private fun personNotSave() {
        if (hasAccValueChanged(currentAccX, currentAccY, currentAccZ) && hasGyroValueChanged(currentGyroX, currentGyroY, currentGyroZ)) {
            emergencyAlertManager.startEmergencyAlert(10000, phoneNumber)
        }
    }
}

