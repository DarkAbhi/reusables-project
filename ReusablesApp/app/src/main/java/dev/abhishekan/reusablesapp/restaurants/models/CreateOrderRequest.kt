package dev.abhishekan.reusablesapp.restaurants.models

data class CreateOrderRequest(
    val item_uuids: List<String>
)