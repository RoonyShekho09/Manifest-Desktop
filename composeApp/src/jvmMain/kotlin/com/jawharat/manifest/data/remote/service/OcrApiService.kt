package com.jawharat.manifest.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface OcrApiService {
    @POST("parse/image")
    suspend fun ocr(
        @Query("base64Image") image: String,
        @Query("OCREngine") engine: String = "3",
        @Query("apikey") apiKey: String = "0cd0e66ab388957",
    ): Response<String>
}
