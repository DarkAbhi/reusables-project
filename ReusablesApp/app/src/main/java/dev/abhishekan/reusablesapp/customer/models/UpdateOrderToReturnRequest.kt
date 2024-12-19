package dev.abhishekan.reusablesapp.customer.models

data class UpdateOrderToReturnRequest(
    val item_uuids: List<String>
)