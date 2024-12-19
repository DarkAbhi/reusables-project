package dev.abhishekan.reusablesapp.delivery_partner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.abhishekan.reusablesapp.data.network.repository.ReusablesRepository
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderResponse
import dev.abhishekan.reusablesapp.vo.ResultWrapper
import dev.abhishekan.reusablesapp.vo.State
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryPartnerViewModel @Inject constructor(private val reusablesRepository: ReusablesRepository) :
    ViewModel() {

    private val _getLiveReturnOrdersResponse =
        Channel<State>(Channel.BUFFERED)
    val getLiveReturnOrdersResponse = _getLiveReturnOrdersResponse.receiveAsFlow()

    fun getLiveReturnOrders() = viewModelScope.launch {
        _getLiveReturnOrdersResponse.send(State.Loading)
        when (
            val response =
                reusablesRepository.getLiveReturnOrders()
        ) {
            is ResultWrapper.GenericError -> {
                _getLiveReturnOrdersResponse.send(State.Failed(response.error?.error))
            }

            ResultWrapper.NetworkError -> {
                _getLiveReturnOrdersResponse.send(State.Failed("Network Error"))
            }

            is ResultWrapper.Success<List<CreateOrderResponse>> -> {
                _getLiveReturnOrdersResponse.send(State.Success(response.value))
            }
        }
    }
}