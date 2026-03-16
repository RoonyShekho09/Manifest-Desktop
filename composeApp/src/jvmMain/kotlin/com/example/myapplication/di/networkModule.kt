package com.example.myapplication.di

import com.example.myapplication.data.remote.service.AppApiService
import com.example.myapplication.data.remote.service.createAppApiService
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

const val BASE_URL = "http://192.168.0.99/"

val networkModule = module {
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

            defaultRequest {
                header("Accept", ContentType.Application.Json)
                header("Content-Type", ContentType.Application.Json)
                // TODO: Replace with token saved in local storage
                header(
                    "Cookie",
                    "I=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY5YjExYTA3NjAwMWI1NDcwOGUzZTYzOSIsImlhdCI6MTc3MzY0ODM5OSwiZXhwIjoxNzczNjc4MDk5LCJpc3MiOiJKYXdoYXJhdCBFcmJpbCJ9.HRQo2bE_Zk0aOEElv0yZwzR8gdk2Kun4I2ngO6tGQT8"
                )
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
}
