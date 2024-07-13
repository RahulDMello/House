package org.example.house

import App
import UserLocation
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.sample
import org.koin.androidx.viewmodel.ext.android.viewModel


@OptIn(FlowPreview::class)
class MainActivity : ComponentActivity(), SensorEventListener {

    private val locationManager by lazy {
         getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private val sensorManager by lazy { getSystemService (Context.SENSOR_SERVICE) as SensorManager }
    private val magnetometer by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }
    private val accelerometer by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
//    private var lastAccelerometerSet = false
//    private var lastMagnetometerSet = false
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)


    private val mutableFlow = MutableStateFlow<UserLocation?>(null)

    val houseViewModel: HouseViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            println("RAHUL - no permissions")
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 5000, 10f
        ) { p0 ->
            println("RAHUL - GPS - location updated ${p0.latitude}, {${p0.longitude}}")
            mutableFlow.value = UserLocation(
                p0.latitude,
                p0.longitude
            )
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER, 5000, 10f
        ) { p0 ->
            println("RAHUL - NETWORK - location updated ${p0.latitude}, {${p0.longitude}}")
            mutableFlow.value = UserLocation(
                p0.latitude,
                p0.longitude
            )
        }

        mutableFlow
            .filterNotNull()
            .onEach { i ->
                println("RAHUL - network request")
                houseViewModel.loadLaunches(i)
            }
            .launchIn(lifecycleScope)

        combine(houseViewModel.state, houseViewModel.orientationState, ::Pair).sample(500)
            .onEach { (house, orientation) ->
                setContent {
                    App(house, orientation)
                }
            }
            .launchIn(lifecycleScope)
    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor === magnetometer) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        } else if (event.sensor === accelerometer) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        }

        updateOrientationAngles()
    }

    override fun onAccuracyChanged(p0: Sensor, p1: Int) {}

    fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // "orientationAngles" now has up-to-date information.
        houseViewModel.orientationState.value = orientationAngles.createOrientation()
    }
}


//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}