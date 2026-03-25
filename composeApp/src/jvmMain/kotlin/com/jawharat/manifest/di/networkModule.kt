package com.jawharat.manifest.di

import com.jawharat.manifest.data.remote.interceptor.AuthInterceptor
import com.jawharat.manifest.data.remote.service.AppApiService
import com.jawharat.manifest.data.remote.service.createAppApiService
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

const val BASE_URL = "http://192.168.0.150/"

val networkModule = module {

    single { AuthInterceptor(get()) }

    single<AppApiService> {
        ktorfit {
            baseUrl(url = BASE_URL)
            httpClient(client = get())

            converterFactories(
                FlowConverterFactory(),
                CallConverterFactory(),
                ResponseConverterFactory()
            )
        }.createAppApiService()
    }

    single {
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            explicitNulls = false
        }

        HttpClient {
            expectSuccess = false
            followRedirects = true

            install(ContentNegotiation) {
                json(
                    json,
                    ContentType.Application.Json
                )
            }

            val authInterceptor = AuthInterceptor(get())
            install(authInterceptor())

            defaultRequest {
                header("Accept", ContentType.Application.Json)
                header("Content-Type", ContentType.Application.Json)
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) =
                        println("HttpClient $message")
                }
            }
        }.apply {
            val authInterceptor: AuthInterceptor = get()
            with(authInterceptor) {
                intercept()
            }
        }
    }
}
