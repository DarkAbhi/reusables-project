package dev.abhishekan.reusablesapp.debug_view.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClearOrdersResponse(
    @Json(name = "message")
    val message: String,
)