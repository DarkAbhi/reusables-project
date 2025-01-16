package dev.abhishekan.reusablesapp.data.network.repository

import dev.abhishekan.reusablesapp.customer.models.UpdateOrderToReturnRequest
import dev.abhishekan.reusablesapp.customer.models.UpdateOrderToReturnResponse
import dev.abhishekan.reusablesapp.debug_view.models.ClearOrdersResponse
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedRequest
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedResponse
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderRequest
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderResponse
import dev.abhishekan.reusablesapp.vo.ResultWrapper

interface ReusablesRepo {

    suspend fun createOrder(
        request: CreateOrderRequest
    ): ResultWrapper<CreateOrderResponse>

    suspend fun updateOrderToReturn(
        request: UpdateOrderToReturnRequest
    ): ResultWrapper<UpdateOrderToReturnResponse>

    suspend fun updateOrderToCompleted(
        request: UpdateOrderToCompletedRequest
    ): ResultWrapper<UpdateOrderToCompletedResponse>

    suspend fun getLiveReturnOrders(): ResultWrapper<List<CreateOrderResponse>>

    suspend fun clearAllOrders(): ResultWrapper<ClearOrdersResponse>

}