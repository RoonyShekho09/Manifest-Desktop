package com.jawharat.manifest.di

import com.jawharat.manifest.data.remote.interceptor.AuthInterceptor
import com.jawharat.manifest.data.remote.service.ManifestApiService
import com.jawharat.manifest.data.remote.service.AppPdfApiService
import com.jawharat.manifest.data.remote.service.MistralApiService
import com.jawharat.manifest.data.remote.service.OcrApiService
import com.jawharat.manifest.data.remote.service.createAppPdfApiService
import com.jawharat.manifest.data.remote.service.createManifestApiService
import com.jawharat.manifest.data.remote.service.createMistralApiService
import com.jawharat.manifest.data.remote.service.createOcrApiService
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
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

const val BASE_URL = "http://192.168.0.150/"
const val OCR_BASE_URL = "https://ocr.space/"

const val MISTRAL_BASE_URL = "https://api.mistral.ai/v1/"

val networkModule = module {

    single { AuthInterceptor(get()) }

    single<MistralApiService> {
        ktorfit {
            baseUrl(url = MISTRAL_BASE_URL)
            httpClient(client = get(named("mistralClient")))

            converterFactories(
                FlowConverterFactory(),
                CallConverterFactory(),
                ResponseConverterFactory()
            )
        }.createMistralApiService()
    }

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

    single<OcrApiService> {
        ktorfit {
            baseUrl(url = OCR_BASE_URL)
            httpClient(client = get(named("manifestClient")))

            converterFactories(
                FlowConverterFactory(),
                CallConverterFactory(),
                ResponseConverterFactory()
            )
        }.createOcrApiService()
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

    single(named("mistralClient")) {
        HttpClient {
            expectSuccess = false
            followRedirects = true

            defaultRequest {
                header("Content-Type", "application/json")
            }

            val json = Json {
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
                explicitNulls = false
            }

            install(ContentNegotiation) {
                json(json, ContentType.Application.Json)
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) =
                        println("HttpClient $message")
                }
            }
        }
    }

    single(named("pdfClient")) {
        HttpClient {
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

        HttpClient {
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
