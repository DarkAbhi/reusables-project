package dev.abhishekan.reusablesapp.data.network.repository

import dev.abhishekan.reusablesapp.customer.models.UpdateOrderToReturnRequest
import dev.abhishekan.reusablesapp.customer.models.UpdateOrderToReturnResponse
import dev.abhishekan.reusablesapp.data.network.helper.safeApiCall
import dev.abhishekan.reusablesapp.data.network.service.ApiService
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedRequest
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedResponse
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderRequest
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderResponse
import dev.abhishekan.reusablesapp.vo.ResultWrapper
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ReusablesRepository @Inject constructor(
    private val apiService: ApiService
) : ReusablesRepo {
    override suspend fun createOrder(request: CreateOrderRequest): ResultWrapper<CreateOrderResponse> {
        return safeApiCall(Dispatchers.IO) {
            apiService.createOrder(
                request = request
            )
        }
    }

    override suspend fun updateOrderToReturn(request: UpdateOrderToReturnRequest): ResultWrapper<UpdateOrderToReturnResponse> {
        return safeApiCall(Dispatchers.IO) {
            apiService.updateOrderToReturn(
                request = request
            )
        }
    }

    override suspend fun updateOrderToCompleted(request: UpdateOrderToCompletedRequest): ResultWrapper<UpdateOrderToCompletedResponse> {
        return safeApiCall(Dispatchers.IO) {
            apiService.updateOrderToCompleted(
                request = request
            )
        }
    }

    override suspend fun getLiveReturnOrders(): ResultWrapper<List<CreateOrderResponse>> {
        return safeApiCall(Dispatchers.IO) {
            apiService.getLiveReturnOrders()
        }
    }

}