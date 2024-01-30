package com.example.alpikacjaratunkowa

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
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
import androidx.core.app.NotificationCompat
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
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs


class MyServices : Service(), SensorEventListener {
    private var isAlreadyNotSafe = false
    private var thresholdAcc: Float = 20.0f
    private var thresholdGyro: Float = 5.0f
    private var lastAccX: Float = 0f
    private var lastAccY: Float = 0f
    private var lastAccZ: Float = 0f
    private var lastGyroX: Float = 0f
    private var lastGyroY: Float = 0f
    private var lastGyroZ: Float = 0f
    private var lastSeenLocation: Location? = null
    private var lastSeenLocationTime = LocalDateTime.now()
    private lateinit var phoneNumber: String
    private var alertDuration: Long = 10000
    private var currentAccX: Float = 0f
    private var currentAccY: Float = 0f
    private var currentAccZ: Float = 0f
    private var currentGyroX: Float = 0f
    private var currentGyroY: Float = 0f
    private var currentGyroZ: Float = 0f
    private var hasGyroChanged :Boolean = false
    private var hasAccChanged :Boolean = false


    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var emergencyAlertManager: EmergencyAlertManager
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var job: Job? = null

    fun registerListeners(){

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("Accelerometer","accelerometer found")
        } else {
            Log.d("Accelerometer", "No accelerometer found")
        }
        if (gyroscope != null) {
            Log.d("Gyroscope","gyroscope found")
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.d("Gyroscope", "No gyroscope found")
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
                        if (!isLocationChanging(location) && !isAlreadyNotSafe){
                            personIsNotSafe()
                        }
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

    private fun isLocationChanging(location: Location): Boolean {
        // Check if there is a previous location and time
        val editor = sharedPreferences.edit()
        editor.putString("lastSeenLocation", "${location.latitude}/${location.longitude}")
        editor.apply()

        if (lastSeenLocation != null && lastSeenLocationTime != null) {
            // Calculate the distance between the current location and the last seen location
            val distance = lastSeenLocation!!.distanceTo(location)

            // Calculate the time difference between the current time and the last seen time
            val timeDifference = Duration.between(lastSeenLocationTime, LocalDateTime.now())

            // Check if the location has changed by more than 500 meters within the last 10 minutes
            if (distance < 500 && timeDifference.toMinutes() >= 10) {
                return false
            }
            if (distance > 500) {
                lastSeenLocation = location
                lastSeenLocationTime = LocalDateTime.now()
            }
        }
        return true
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

//                if (hasAccValueChanged(x, y, z)) {
//                    val values = "X: $x\nY: $y\nZ: $z"
//                    Log.d("Acc Values","Accelerometer Values:\n$values")
//                }
                hasAccValueChanged(currentAccX, currentAccY, currentAccZ)
            }

            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                currentGyroX = event.values[0]
                currentGyroY = event.values[1]
                currentGyroZ = event.values[2]

//                if (hasGyroValueChanged(x, y, z)) {
//                    val values = "X: $x\nY: $y\nZ: $z"
//                    Log.d("Gyro Values","Gyroscope Values:\n$values")
//                }
                hasGyroValueChanged(currentGyroX, currentGyroY, currentGyroZ)

            }
        }


        isPersonSave()
    }

override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    SensorManager.SENSOR_STATUS_ACCURACY_HIGH
}

override fun onDestroy() {
    super.onDestroy()
    if (::sensorManager.isInitialized) {
        sensorManager.unregisterListener(this)
    }
}
    private fun hasAccValueChanged(x: Float, y: Float, z: Float) {


        val deltaX = abs(x - lastAccX)
        val deltaY = abs(y - lastAccY)
        val deltaZ = abs(z - lastAccZ)
        // Check if any of the differences is greater than the threshold
        if (deltaX > thresholdAcc || deltaY > thresholdAcc || deltaZ > thresholdAcc) {
            // Values have changed
            lastAccX = x
            lastAccY = y
            lastAccZ = z
            hasAccChanged = true
            return
        }

        // Values have not changed
        hasAccChanged = false
        return
    }

    private fun hasGyroValueChanged(x: Float, y: Float, z: Float) {

        val deltaX = abs(x - lastGyroX)
        val deltaY = abs(y - lastGyroY)
        val deltaZ = abs(z - lastGyroZ)

        // Check if any of the differences is greater than the threshold
        if (deltaX > thresholdGyro || deltaY > thresholdGyro || deltaZ > thresholdGyro) {
            // Values have changed
            lastGyroX = x
            lastGyroY = y
            lastGyroZ = z
            hasGyroChanged = true
            return
        }

        // Values have not changed
        hasGyroChanged =  false
        return
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun isPersonSave() {
        if(hasGyroChanged && hasAccChanged && !isAlreadyNotSafe) {
            personIsNotSafe()
        }
    }
    private fun personIsNotSafe(){
        isAlreadyNotSafe = true
        phoneNumber = sharedPreferences.getString("phoneNumber", "888119218") ?: "888119218"
        alertDuration = sharedPreferences.getString("alertDuration", "10000")?.toLong() ?: 10000
        Log.d("PersonNotSafe", "Making notification")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = "com.currency.usdtoinr"
            val channelName = "My Background Service"
            val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setContentTitle("")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)

            val dialogIntent = Intent(this, AlertActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(dialogIntent)
            Log.d("sk", "After startforeground executed")

        } else {

            var notificationIntent = Intent(this, MainActivity::class.java)


            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

            Log.d("PersonNotSafe", "Notification bilder")
            var notification = Notification.Builder(this, "your_channel_id")
                .setContentTitle("")
                .setContentText("")
                .setContentIntent(pendingIntent)
                .setTicker("")
                .build()

            startForeground(2, notification)
            Log.d("PersonNotSafe", "Crerate activiti")
            val dialogIntent = Intent(this, AlertActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.d("PersonNotSafe", "before activity start")
            startActivity(dialogIntent)
        }
//        emergencyAlertManager.startEmergencyAlert(alertDuration, phoneNumber, lastSeenLocation)
//        Log.d("PersonNotSafe", "Safe")

    }

}

