package kzs.th000.curioushub.core.network

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kzs.th000.curioushub.core.constants.API_BASE_URL
import kzs.th000.curioushub.core.constants.BASE_URL

data class AppUriParam(val key: String, val value: String)

class AppHttpClient {
    private val _client: HttpClient = HttpClient(OkHttp)

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        isLenient = true
    }

    suspend fun getJson(
        target: Uri,
        accept: ContentType = ContentType("application", "json"),
        headers: List<AppUriParam>? = null,
        authorization: String? = null,
    ): JsonObject? {
        val resp =
            _client.post(target.toString()) {
                accept(accept)
                headers?.forEach { it -> this.header(it.key, it.value) }
                authorization?.let { header(HttpHeaders.Authorization, it) }
            }

        if (resp.status != HttpStatusCode.OK) {
            // TODO: Error handling.
            Log.e("AppHttpClient::getJson", "response status code ${resp.status}")
            return null
        }

        return resp
            .readRawBytes()
            .decodeToString()
            .let { it -> json.parseToJsonElement(it) }
            .let { it ->
                when (it) {
                    is JsonArray,
                    is JsonPrimitive,
                    JsonNull -> null
                    is JsonObject -> it
                }
            }
    }

    suspend fun postJson(target: Uri, data: Map<String, String>? = null): JsonObject? {
        val resp = _client.post(target.toString()) { accept(ContentType("application", "json")) }

        if (resp.status != HttpStatusCode.OK) {
            // TODO: Error handling.
            return null
        }

        return resp
            .readRawBytes()
            .decodeToString()
            .let { it -> json.parseToJsonElement(it) }
            .let { it ->
                when (it) {
                    is JsonArray,
                    is JsonPrimitive,
                    JsonNull -> null
                    is JsonObject -> it
                }
            }
    }

    companion object {
        fun buildGhTarget(
            paths: List<String>? = null,
            queryParameters: List<AppUriParam>? = null,
        ): Uri =
            BASE_URL.toUri()
                .buildUpon()
                .apply {
                    paths?.forEach { it -> this.appendPath(it) }
                    queryParameters?.forEach { it -> this.appendQueryParameter(it.key, it.value) }
                }
                .build()

        fun buildGhApiTarget(
            paths: List<String>? = null,
            queryParameters: List<AppUriParam>? = null,
        ): Uri =
            API_BASE_URL.toUri()
                .buildUpon()
                .apply {
                    paths?.forEach { it -> this.appendPath(it) }
                    queryParameters?.forEach { it -> this.appendQueryParameter(it.key, it.value) }
                }
                .build()
    }
}
