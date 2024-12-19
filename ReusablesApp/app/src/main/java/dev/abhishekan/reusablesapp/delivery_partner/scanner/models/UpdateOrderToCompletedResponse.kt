package dev.abhishekan.reusablesapp.delivery_partner.scanner.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateOrderToCompletedResponse(
    @Json(name = "message")
    val message: String,
)