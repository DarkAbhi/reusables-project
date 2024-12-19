package dev.abhishekan.reusablesapp.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.abhishekan.reusablesapp.customer.models.UpdateOrderToReturnRequest
import dev.abhishekan.reusablesapp.customer.models.UpdateOrderToReturnResponse
import dev.abhishekan.reusablesapp.data.network.repository.ReusablesRepository
import dev.abhishekan.reusablesapp.vo.ResultWrapper
import dev.abhishekan.reusablesapp.vo.State
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val reusablesRepository: ReusablesRepository
) : ViewModel() {

    private val _updateOrderToReturnResponse =
        Channel<State>(Channel.BUFFERED)
    val updateOrderToReturnResponse = _updateOrderToReturnResponse.receiveAsFlow()

    private val _scannedCodes = MutableLiveData<List<String>>()
    val scannedCodes: LiveData<List<String>> = _scannedCodes

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

    fun updateOrderToReturn(request: UpdateOrderToReturnRequest) = viewModelScope.launch {
        _updateOrderToReturnResponse.send(State.Loading)
        when (
            val response =
                reusablesRepository.updateOrderToReturn(request = request)
        ) {
            is ResultWrapper.GenericError -> {
                _updateOrderToReturnResponse.send(State.Failed(response.error?.error))
            }

            ResultWrapper.NetworkError -> {
                _updateOrderToReturnResponse.send(State.Failed("Network Error"))
            }

            is ResultWrapper.Success<UpdateOrderToReturnResponse> -> {
                _updateOrderToReturnResponse.send(State.Success(response.value))
            }
        }
    }

}