package dev.abhishekan.reusablesapp.customer.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateOrderToReturnResponse(
    @Json(name = "message")
    val message: String,
)