package com.jawharat.manifest.data.remote.datasource

import de.jensklingenberg.ktorfit.Response
import kotlinx.coroutines.delay


interface BaseRemoteDataSource {

    suspend fun <T, R> callApi(
        apiCall: suspend () -> Response<T>,
        mapper: (T) -> R
    ) = checkIfSuccessful(result = apiCall(), mapper = mapper)

    suspend fun <T, R> callApiWithRetry(
        apiCall: suspend () -> Response<T>,
        mapper: (T) -> R,
    ) = retry(delayMillis = 10000, maxRetries = 4) {
        callApi(apiCall = apiCall, mapper = mapper)
    }

    suspend fun <T> retry(
        delayMillis: Long,
        maxRetries: Int,
        block: suspend () -> T
    ): T {
        repeat(maxRetries - 1) {
            runCatching {
                return block()
            }.onFailure {
                delay(delayMillis)
            }
        }
        return block()
    }

    private fun <T, R> checkIfSuccessful(
        result: Response<T>,
        mapper: (T) -> R
    ) = run {
        when {
            result.isSuccessful && result.body() != null -> {
                val apiResponse = result.body()!!

                if (result.isSuccessful)
                    kotlin.Result.success(mapper(apiResponse))
                else
                    kotlin.Result.failure(Exception(result.message))
            }

            else -> {
                kotlin.Result.failure(
                    Exception(
                        result.errorBody()?.toString() ?: "Unknown API error"
                    )
                )
            }
        }
    }
}

data class ApiResponse<T>(
    val data: T?,
    val status: String,
    val message: String? = null
)

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: String? = null) : Result<Nothing>()
}

fun <T> ApiResponse<T>.toResult(): Result<T> {
    return when {
        status == "success" && data != null -> Result.Success(data)
        else -> Result.Error(message ?: "Unknown error", status)
    }
}
