package com.example.kompas

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import java.util.ArrayList

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var imgCompass: ImageView
    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private var azimuth: Float = 0f
    private var currectAzimuth: Float = 0f
    private lateinit var mSensorManager: SensorManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgCompass = findViewById(R.id.img_compass)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME)
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)

    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val alpha = 0.97f
        synchronized(this) {
            if (p0!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
                mGravity[0] = alpha*mGravity[0] + (1 - alpha)*p0.values[0]
                mGravity[1] = alpha*mGravity[1] + (1 - alpha)*p0.values[1]
                mGravity[2] = alpha*mGravity[2] + (1 - alpha)*p0.values[2]
            }

            if (p0!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * p0.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * p0.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * p0.values[2]
            }

            val R = FloatArray(9)
            val I = FloatArray(9)
            val succes = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)

            if (succes) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                //
                val animation = RotateAnimation(-currectAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                currectAzimuth = azimuth
                animation.duration = 500
                animation.repeatCount = 0
                animation.fillAfter = true
                 imgCompass.startAnimation(animation)
            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


}


