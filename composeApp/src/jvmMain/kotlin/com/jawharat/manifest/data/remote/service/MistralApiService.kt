package com.jawharat.manifest.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import kotlinx.serialization.Serializable

interface MistralApiService {
    @POST("ocr")
    suspend fun ocr(
        @Header("Authorization") apiKey: String = "Bearer: 97ZQlsV45YrDusgZRwjArWGbh3nerFPb",
        @Body body: MistralRequestBody
    ): Response<Any>
}

@Serializable
data class MistralRequestBody(
    val model: String = "mistral-ocr-latest",
    val document: Document

)

@Serializable
data class Document(
    val type: String = "document_url",
    val documentUrl: String,
)
