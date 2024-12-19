package dev.abhishekan.reusablesapp.vo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DjangoDetailErrorResponse(
    @Json(name = "detail")
    val error: String, // this is the translated error shown to the user directly from the API
)