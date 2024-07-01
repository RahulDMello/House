data class UserLocation(
    val lat: Double,
    val lon: Double
)

expect fun getUserLocation(): UserLocation

class Listings {
    fun getListings(): String = "${getUserLocation().lat}, ${getUserLocation().lon}"

    fun getListings(location: UserLocation): String = "${location.lat} ${location.lon}"
}