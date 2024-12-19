package dev.abhishekan.reusablesapp.restaurants.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateOrderResponse(
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "updated_at")
    val updatedAt: String
)