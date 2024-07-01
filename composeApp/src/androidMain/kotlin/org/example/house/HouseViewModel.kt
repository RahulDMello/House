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
