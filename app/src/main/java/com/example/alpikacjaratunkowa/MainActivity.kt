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

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var lastAccX:Float = 0f
    private var lastAccY:Float = 0f
    private var lastAccZ:Float = 0f
    private var lastGyroX:Float = 0f
    private var lastGyroY:Float = 0f
    private var lastGyroZ:Float = 0f

    private lateinit var sensorManager: SensorManager
    private val accelerometer: Sensor? = null
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
        when(event.sensor.type){
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                if (hasAccValueChanged(x, y, z)){
                    val values = "X: $x\nY: $y\nZ: $z"
                    accelerometerValues.text = "Accelerometer Values:\n$values"
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

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
        val threshold = 0.5f // Adjust this threshold based on your sensitivity requirements

        val deltaX = Math.abs(x - lastAccX)
        val deltaY = Math.abs(y - lastAccY)
        val deltaZ = Math.abs(z - lastAccZ)

        // Check if any of the differences is greater than the threshold
        if (deltaX > threshold || deltaY > threshold || deltaZ > threshold) {
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
        val threshold = 0.5f // Adjust this threshold based on your sensitivity requirements

        val deltaX = Math.abs(x - lastGyroX)
        val deltaY = Math.abs(y - lastGyroY)
        val deltaZ = Math.abs(z - lastGyroZ)

        // Check if any of the differences is greater than the threshold
        if (deltaX > threshold || deltaY > threshold || deltaZ > threshold) {
            // Values have changed
            lastGyroX = x
            lastGyroY = y
            lastGyroZ = z
            return true
        }

        // Values have not changed
        return false
    }
}