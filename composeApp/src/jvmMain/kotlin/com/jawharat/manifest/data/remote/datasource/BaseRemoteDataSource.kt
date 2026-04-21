package com.jawharat.manifest.data.remote.datasource

import com.jawharat.manifest.data.remote.exceptions.TooManyRequestsException
import com.jawharat.manifest.domain.exceptions.NetworkException
import de.jensklingenberg.ktorfit.Response
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json


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

    private suspend fun <T, R> checkIfSuccessful(
        result: Response<T>,
        mapper: (T) -> R
    ) = run {
        when {
            result.isSuccessful && result.body() != null -> {
                val apiResponse = result.body()!!

                if (result.isSuccessful)
                    Result.success(mapper(apiResponse))
                else
                    Result.failure(Exception(result.message))
            }

            result.status == HttpStatusCode.Unauthorized -> throw NetworkException.SessionExpiredException()

            result.status == HttpStatusCode.Locked -> throw NetworkException.BlockedException()

            result.status == HttpStatusCode.Found -> @Suppress("UNCHECKED_CAST")
            Result.success(null as R)

            else -> {
                Result.failure(
                    Exception(
                        result.errorBody()?.toString() ?: "Unknown API error"
                    )
                )
            }
        }
    }
}

suspend fun HttpResponse.toTooManyRequestsException(): NetworkException.ManifestSubmittedRecentlyException {
    val exception = Json.decodeFromString<TooManyRequestsException>(this.bodyAsText())
    return NetworkException.ManifestSubmittedRecentlyException(
        message = exception.message.orEmpty(),
        retryInSeconds = exception.retryAfter ?: 0
    )
}
