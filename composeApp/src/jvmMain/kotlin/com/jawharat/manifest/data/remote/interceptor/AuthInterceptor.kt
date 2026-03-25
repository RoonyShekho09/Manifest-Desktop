package com.jawharat.manifest.data.remote.interceptor

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.remote.service.AppApiService.Companion.NO_AUTH_HEADER_KEY
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header

class AuthInterceptor(private val localDataSource: AppLocalDataSource) {

    private val name = AuthInterceptor::class.simpleName

    private val plugin = createClientPlugin("${name}Plugin") {
        onRequest { request, _ ->
            val skipAuthorization = request.headers.contains(NO_AUTH_HEADER_KEY)
            if (!skipAuthorization)
                request.header("Cookie", "I=${localDataSource.token}")
        }
    }

    fun HttpClient.intercept() = plugin(HttpSend).intercept { request ->
        val originalCall = execute(request)

        if (originalCall.response.status.value == 401 && !originalCall.request.headers.contains(
                NO_AUTH_HEADER_KEY
            )
        ) {
            localDataSource.clearDataStore()
            originalCall
        } else {
            originalCall
        }
    }


    operator fun invoke(): ClientPlugin<Unit> = plugin
}
