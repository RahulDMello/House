package network

import UserLocation
import entity.HouseInfo
import entity.Houses
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import network.LocalIP.HOST
import network.LocalIP.PORT

class HouseApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    suspend fun getNearByHouses(userLocation: UserLocation): Houses {
        return httpClient.get(HOUSE_URL) {
            url {
                parameters.append("lat", userLocation.lat.toString())
                parameters.append("lon", userLocation.lon.toString())
            }
        }.body()
    }

    companion object {
        private const val HOUSE_URL="$HOST:$PORT/house"
    }
}
