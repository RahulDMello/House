package org.example.house

import HouseSDK
import UserLocation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import entity.HouseInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class HouseViewModel(private val sdk: HouseSDK) : ViewModel() {
    private val _state = MutableStateFlow<HouseInfoScreenState?>(null)
    val state: Flow<HouseInfoScreenState> = _state.filterNotNull()

    val orientationState = MutableStateFlow(Orientation(0f, 0f, 0f))

    fun loadLaunches(userLocation: UserLocation) {
        viewModelScope.launch {
            _state.value = HouseInfoScreenState(houses = emptyList())
            try {
                val launches = sdk.getHouses(
                    userLocation
                )
                _state.value = HouseInfoScreenState(houses = launches)
            } catch (e: Exception) {
                _state.value = HouseInfoScreenState(houses = emptyList())
            }
        }
    }
}

data class HouseInfoScreenState(
    val houses: List<HouseInfo> = emptyList()
)

/**
 * Holds the orientation angles.
 *
 * @param azimuth (degrees of rotation about the -z axis). This is the angle between the device's current compass direction and magnetic north. If the top edge of the device faces magnetic north, the azimuth is 0 degrees; if the top edge faces south, the azimuth is 180 degrees. Similarly, if the top edge faces east, the azimuth is 90 degrees, and if the top edge faces west, the azimuth is 270 degrees.
 * @param pitch (degrees of rotation about the x axis). This is the angle between a plane parallel to the device's screen and a plane parallel to the ground. If you hold the device parallel to the ground with the bottom edge closest to you and tilt the top edge of the device toward the ground, the pitch angle becomes positive. Tilting in the opposite direction— moving the top edge of the device away from the ground—causes the pitch angle to become negative. The range of values is -90 degrees to 90 degrees.
 * @param roll (degrees of rotation about the y axis). This is the angle between a plane perpendicular to the device's screen and a plane perpendicular to the ground. If you hold the device parallel to the ground with the bottom edge closest to you and tilt the left edge of the device toward the ground, the roll angle becomes positive. Tilting in the opposite direction—moving the right edge of the device toward the ground— causes the roll angle to become negative. The range of values is -180 degrees to 180 degrees.
 */
data class Orientation(
    val azimuth: Float,
    val pitch: Float,
    val roll: Float
) {
    override fun toString(): String {
        return "azimuth: ${azimuth.toDegrees()}, pitch: ${pitch.toDegrees()}, roll: ${roll.toDegrees()}"
    }

    fun Float.toDegrees() = this * 180 / Math.PI
}

fun FloatArray.createOrientation() =
    Orientation(this[0], this[1], this[2])
