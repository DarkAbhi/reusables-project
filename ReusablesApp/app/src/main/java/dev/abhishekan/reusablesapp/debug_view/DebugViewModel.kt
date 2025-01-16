package dev.abhishekan.reusablesapp.debug_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.abhishekan.reusablesapp.data.network.repository.ReusablesRepository
import dev.abhishekan.reusablesapp.debug_view.models.ClearOrdersResponse
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedRequest
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedResponse
import dev.abhishekan.reusablesapp.vo.ResultWrapper
import dev.abhishekan.reusablesapp.vo.State
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val reusablesRepository: ReusablesRepository
): ViewModel() {

    private val _clearAllOrdersResponse =
        Channel<State>(Channel.BUFFERED)
    val clearAllOrdersResponse = _clearAllOrdersResponse.receiveAsFlow()

    fun clearAllOrders() = viewModelScope.launch {
        _clearAllOrdersResponse.send(State.Loading)
        when (
            val response =
                reusablesRepository.clearAllOrders()
        ) {
            is ResultWrapper.GenericError -> {
                _clearAllOrdersResponse.send(State.Failed(response.error?.error))
            }

            ResultWrapper.NetworkError -> {
                _clearAllOrdersResponse.send(State.Failed("Network Error"))
            }

            is ResultWrapper.Success<ClearOrdersResponse> -> {
                _clearAllOrdersResponse.send(State.Success(response.value))
            }
        }
    }

}