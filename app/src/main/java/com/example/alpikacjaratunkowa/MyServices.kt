package com.example.alpikacjaratunkowa

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build

import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyServices: Service(),SensorEventListener {


    private var thresholdAcc: Float = 0.5f
    private var thresholdGyro: Float = 0.5f
    private var lastAccX: Float = 0f
    private var lastAccY: Float = 0f
    private var lastAccZ: Float = 0f
    private var lastGyroX: Float = 0f
    private var lastGyroY: Float = 0f
    private var lastGyroZ: Float = 0f
    private var lastSeenLocation: Location? = null
    private var phoneNumber: String = "888119218"
    private var currentAccX: Float = 0f
    private var currentAccY: Float = 0f
    private var currentAccZ: Float = 0f
    private var currentGyroX: Float = 0f
    private var currentGyroY: Float = 0f
    private var currentGyroZ: Float = 0f


    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var emergencyAlertManager: EmergencyAlertManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var job: Job? = null
    fun registerListeners(){
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("Accelerometer","accelerometer found")
        } else {
            Log.d("Accelerometer","No accelerometer found")
        }
        if (gyroscope != null) {
            Log.d("Gyroscope","gyroscope found")
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d("Gyroscope","No gyroscope found")
        }

    }
    fun unRegisterListeners(){
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, gyroscope)
    }
    private lateinit var wakeLock: PowerManager.WakeLock
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // do your jobs here
        println("IM IN THE FUCKING FOREGROUND")
        Log.d("MyServices", "IM IN THE FOREGROUND")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope= sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        emergencyAlertManager = EmergencyAlertManager(this)


        val channelId = "your_channel_id"
        val channelName = "Your Channel Name"
        val channelDescription = "Your Channel Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = channelDescription

        // Register the channel with the system
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        registerListeners()

        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val notification = Notification.Builder(this@MyServices, "your_channel_id")
                    .setContentTitle("Your Service is Running")
                    .setContentText("Collecting Sensor Data")
                    .build()

                startForeground(1, notification)
                delay(1000) // Set the interval in milliseconds
//                println(sensorManager)
                println("Printing every 1 second in the loop")
                Log.d("MyServices", "IM IN THE FOREGROUND")

            }
        }
        // Obsługa kliknięcia przycisku
//        startEmergencyButton.setOnClickListener {
        // Rozpocznij alert w przypadku kliknięcia przycisku
//            emergencyAlertManager.startEmergencyAlert(10000, "lastSeenLocation")
//        }


            val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    for (location in p0.locations) {
                        lastSeenLocation = location
                        Log.d("GPS",SMSMessageUtils.getCity(
                            location.latitude,
                            location.longitude,
                            this@MyServices
                        )
                        )
                    }
                }
            }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){}
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {

                currentAccX = event.values[0]
                currentAccY = event.values[1]
                currentAccZ = event.values[2]

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                if (hasAccValueChanged(x, y, z)) {
                    val values = "X: $x\nY: $y\nZ: $z"
                    Log.d("Acc Values","Accelerometer Values:\n$values")
                }

            }

            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                currentGyroX = event.values[0]
                currentGyroY = event.values[1]
                currentGyroZ = event.values[2]

                if (hasGyroValueChanged(x, y, z)) {
                    val values = "X: $x\nY: $y\nZ: $z"
                    Log.d("Gyro Values","Gyroscope Values:\n$values")
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

    private fun hasAccValueChanged(x: Float, y: Float, z: Float): Boolean {

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

    private fun hasGyroValueChanged(x: Float, y: Float, z: Float): Boolean {

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
        if (hasAccValueChanged(currentAccX, currentAccY, currentAccZ) && hasGyroValueChanged(
                currentGyroX,
                currentGyroY,
                currentGyroZ
            )
        ) {
            emergencyAlertManager.startEmergencyAlert(10000, phoneNumber, lastSeenLocation)
        }
    }
}