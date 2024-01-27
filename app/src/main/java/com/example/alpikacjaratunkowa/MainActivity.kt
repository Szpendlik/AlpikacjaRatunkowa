package com.example.alpikacjaratunkowa

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity(),SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private val acceletometer: Sensor? = null
    private val gyroscope: Sensor? = null
    private lateinit var accelerometerValues: TextView
    private lateinit var gyroscopeValues: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accelerometerValues = findViewById(R.id.accelerometrValues)
        gyroscopeValues = findViewById(R.id.gyroscopeValues)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val  gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (accelerometer != null){
            sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL)
        }else {
            accelerometerValues.text = "No accelerometer found"
        }

        if(gyroscope != null){
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        }else{
            gyroscopeValues.text = "No gyroscope found"
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
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE){
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val values = "X: $x\nY: $y\nZ: $z"
            gyroscopeValues.text = "Accelerometer Values:\n$values"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        SensorManager.SENSOR_STATUS_ACCURACY_HIGH
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}