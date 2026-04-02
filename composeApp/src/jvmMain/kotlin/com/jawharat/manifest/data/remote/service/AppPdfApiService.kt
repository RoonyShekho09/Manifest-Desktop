package com.jawharat.manifest.data.remote.service

import com.jawharat.manifest.data.remote.model.SubmitManifestRequestBody
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.statement.HttpResponse

interface AppPdfApiService {
    @POST("manifests")
    suspend fun submitManifest(@Body body: SubmitManifestRequestBody): HttpResponse
}