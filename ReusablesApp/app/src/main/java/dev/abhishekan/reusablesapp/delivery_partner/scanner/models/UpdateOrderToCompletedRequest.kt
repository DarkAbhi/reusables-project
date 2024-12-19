package dev.abhishekan.reusablesapp.delivery_partner.scanner.models

data class UpdateOrderToCompletedRequest(
    val item_uuids: List<String>
)