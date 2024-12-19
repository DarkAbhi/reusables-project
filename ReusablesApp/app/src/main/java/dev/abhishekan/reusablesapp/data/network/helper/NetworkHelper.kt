package dev.abhishekan.reusablesapp.data.network.helper

import com.squareup.moshi.Moshi
import dev.abhishekan.reusablesapp.vo.DjangoDetailErrorResponse
import dev.abhishekan.reusablesapp.vo.ErrorResponse
import dev.abhishekan.reusablesapp.vo.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

/**
 * Safe api call
 *
 * @param T
 * @param dispatcher
 * @param apiCall
 * @receiver
 * @return
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T,
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ResultWrapper.GenericError(code, errorResponse)
                }

                else -> {
                    ResultWrapper.GenericError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            Timber.e(it.toString())

            val moshi = Moshi.Builder().build()
            val errorAdapter = moshi.adapter(ErrorResponse::class.java)
            val djangoDetailErrorAdapter = moshi.adapter(DjangoDetailErrorResponse::class.java)

            // Try parsing as ErrorResponse
            val errorResponse = errorAdapter.fromJson(it)

            // If parsing as ErrorResponse fails, try parsing as DjangoDetailErrorResponse
            if (errorResponse == null) {
                val djangoDetailErrorResponse = djangoDetailErrorAdapter.fromJson(it)
                // If parsing as DjangoDetailErrorResponse succeeds, create an ErrorResponse from it
                if (djangoDetailErrorResponse != null) {
                    ErrorResponse(djangoDetailErrorResponse.error)
                } else {
                    null
                }
            } else {
                errorResponse
            }
        }
    } catch (exception: Exception) {
        Timber.e(exception)
        null
    }
}