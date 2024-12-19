package dev.abhishekan.reusablesapp.delivery_partner.scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.abhishekan.reusablesapp.data.network.repository.ReusablesRepository
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedRequest
import dev.abhishekan.reusablesapp.delivery_partner.scanner.models.UpdateOrderToCompletedResponse
import dev.abhishekan.reusablesapp.vo.ResultWrapper
import dev.abhishekan.reusablesapp.vo.State
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryPartnerScannerViewModel @Inject constructor(
    private val reusablesRepository: ReusablesRepository
) : ViewModel() {

    private val _updateOrderToCompletedResponse =
        Channel<State>(Channel.BUFFERED)
    val updateOrderToCompletedResponse = _updateOrderToCompletedResponse.receiveAsFlow()

    // Backing property to hold the mutable live data
    private val _scannedCodes = MutableLiveData<List<String>>()

    // Publicly exposed immutable LiveData
    val scannedCodes: LiveData<List<String>> get() = _scannedCodes

    init {
        // Initialize with an empty list
        _scannedCodes.value = emptyList()
    }

    // Function to add a new code to the list
    fun addScannedCode(code: String) {
        val currentList = _scannedCodes.value ?: emptyList()
        _scannedCodes.value = currentList + code
    }

    // Function to clear all scanned codes
    fun clearScannedCodes() {
        _scannedCodes.value = emptyList()
    }

    fun updateOrderToCompleted(request: UpdateOrderToCompletedRequest) = viewModelScope.launch {
        _updateOrderToCompletedResponse.send(State.Loading)
        when (
            val response =
                reusablesRepository.updateOrderToCompleted(request = request)
        ) {
            is ResultWrapper.GenericError -> {
                _updateOrderToCompletedResponse.send(State.Failed(response.error?.error))
            }

            ResultWrapper.NetworkError -> {
                _updateOrderToCompletedResponse.send(State.Failed("Network Error"))
            }

            is ResultWrapper.Success<UpdateOrderToCompletedResponse> -> {
                _updateOrderToCompletedResponse.send(State.Success(response.value))
            }
        }
    }

}