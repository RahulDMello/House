package org.example.house

import App
import UserLocation
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val locationManager by lazy {
         getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 5000, 10f
        ) { p0 ->
            mutableFlow.value = UserLocation(
                p0.latitude,
                p0.longitude
            )
        }

        mutableFlow
            .filterNotNull()
            .onEach { i ->
                houseViewModel.loadLaunches(i)
            }
            .launchIn(lifecycleScope)

        houseViewModel
            .state
            .onEach { i ->
                setContent {
                    App(i)
                }
            }
            .launchIn(lifecycleScope)
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}