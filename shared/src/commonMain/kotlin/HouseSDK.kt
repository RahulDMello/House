import network.HouseApi

class HouseSDK(val api: HouseApi) {
    suspend fun getHouses(userLocation: UserLocation) = api.getNearByHouses(userLocation).data
}