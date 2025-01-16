package dev.abhishekan.reusablesapp.data.network.service

import dev.abhishekan.reusablesapp.customer.models.UpdateOrderToReturnRequest
import dev.abhishekan.reusablesapp.customer.models.UpdateOrderToReturnResponse
import dev.abhishekan.reusablesapp.debug_view.models.ClearOrdersResponse
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedRequest
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedResponse
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderRequest
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("service/orders/create/")
    suspend fun createOrder(@Body request: CreateOrderRequest): CreateOrderResponse

    @POST("service/orders/return/")
    suspend fun updateOrderToReturn(@Body request: UpdateOrderToReturnRequest): UpdateOrderToReturnResponse

    @POST("service/orders/completed/")
    suspend fun updateOrderToCompleted(@Body request: UpdateOrderToCompletedRequest): UpdateOrderToCompletedResponse

    @GET("service/orders/live-return-orders/")
    suspend fun getLiveReturnOrders(): List<CreateOrderResponse>

    @POST("service/orders/clear-all/")
    suspend fun clearAllOrders(): ClearOrdersResponse

}