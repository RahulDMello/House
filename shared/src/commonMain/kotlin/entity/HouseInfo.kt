package entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class HouseInfo(
    val id: Int,
    val type: String,
    @SerialName("addresspath")
    val addressPath: String,
    val price: Double,
    val distance: Double
) {
    override fun toString(): String {
        return addressPath
    }
}

@Serializable
data class Houses(
    val data: List<HouseInfo>
)
