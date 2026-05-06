package com.jawharat.manifest.di

import com.jawharat.manifest.data.local.datasource.AppLocalDataSource
import com.jawharat.manifest.data.remote.interceptor.AuthInterceptor
import com.jawharat.manifest.data.remote.service.ManifestApiService
import com.jawharat.manifest.data.remote.service.AppPdfApiService
import com.jawharat.manifest.data.remote.service.createAppPdfApiService
import com.jawharat.manifest.data.remote.service.createManifestApiService
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val BASE_URL = "http://192.168.0.128/"

val networkModule = module {

    single { AuthInterceptor(get()) }

    single<ManifestApiService> {
        ktorfit {
            baseUrl(url = BASE_URL)
            httpClient(client = get(named("manifestClient")))

            converterFactories(
                FlowConverterFactory(),
                CallConverterFactory(),
                ResponseConverterFactory()
            )
        }.createManifestApiService()
    }

    single<AppPdfApiService> {
        ktorfit {
            baseUrl(url = BASE_URL)
            httpClient(client = get(named("pdfClient")))

            converterFactories(
                FlowConverterFactory(),
                CallConverterFactory(),
                ResponseConverterFactory()
            )
        }.createAppPdfApiService()
    }

    single(named("ocrClient")) {
        HttpClient(CIO) {

            val localDataSource: AppLocalDataSource by inject<AppLocalDataSource>()

            defaultRequest {
                header("Content-Type", "application/json")
                header("Cookie", "I=${localDataSource.token}")
            }

            install(HttpTimeout) {
                this.requestTimeoutMillis = 20000
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }

            expectSuccess = true
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) =
                        println("OcrHttpClient $message")
                }
            }
        }
    }

    single(named("checkUpdatesClient")) {
        HttpClient(CIO) {
            expectSuccess = true
            followRedirects = true

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(Logging) {
                level = LogLevel.BODY
                logger = object : Logger {
                    override fun log(message: String) =
                        println("HttpClient $message")
                }
            }
        }
    }

    single(named("pdfClient")) {
        HttpClient(CIO) {
            expectSuccess = false
            followRedirects = true

            defaultRequest {
                header("Content-Type", "application/json")
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) =
                        println("HttpClient $message")
                }
            }

            val authInterceptor = AuthInterceptor(get())
            install(authInterceptor())
        }
    }

    single(named("manifestClient")) {
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            coerceInputValues = true
            explicitNulls = false
        }

        HttpClient(CIO) {
            expectSuccess = false
            followRedirects = true

            install(ContentNegotiation) {
                json(json, ContentType.Application.Json)
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            val authInterceptor = AuthInterceptor(get())
            install(authInterceptor())

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
