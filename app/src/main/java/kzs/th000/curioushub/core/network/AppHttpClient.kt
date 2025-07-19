package kzs.th000.curioushub.core.network

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kzs.th000.curioushub.core.constants.API_BASE_URL
import kzs.th000.curioushub.core.constants.BASE_URL
import kzs.th000.curioushub.core.exceptions.AppEither
import kzs.th000.curioushub.core.exceptions.AppException

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
    ): AppEither<JsonObject> = either {
        val resp =
            _client.get(target.toString()) {
                accept(accept)
                headers?.forEach { it -> this.header(it.key, it.value) }
                authorization?.let { header(HttpHeaders.Authorization, it) }
            }

        ensure(resp.status == HttpStatusCode.OK) {
            Log.e("AppHttpClient::getJson", "response status code ${resp.status}")
            AppException.HttpRequestFailed(
                method = "GET",
                target = target,
                statusCode = resp.status.value,
            )
        }

        val json =
            resp.readRawBytes().decodeToString().let { it ->
                catch({ json.parseToJsonElement(it) }) { e: SerializationException ->
                    raise(
                        AppException.SerializationFailure(
                            "failed to deserialize response of $target: ${e.message}"
                        )
                    )
                }
            }

        ensure(json is JsonObject) { raise(AppException.SerializationFailure("invalid data")) }
        json
    }

    suspend fun postJson(target: Uri, data: Map<String, String>? = null): AppEither<JsonObject> =
        either {
            val resp =
                _client.post(target.toString()) { accept(ContentType("application", "json")) }

            ensure(resp.status == HttpStatusCode.OK) {
                Log.e("AppHttpClient::postJson", "response status code ${resp.status}")
                AppException.HttpRequestFailed(
                    method = "POST",
                    target = target,
                    statusCode = resp.status.value,
                )
            }

            val json =
                resp.readRawBytes().decodeToString().let { it ->
                    catch({ json.parseToJsonElement(it) }) { e: SerializationException ->
                        raise(
                            AppException.SerializationFailure(
                                "failed to deserialize response of $target: ${e.message}"
                            )
                        )
                    }
                }

            ensure(json is JsonObject) { raise(AppException.SerializationFailure("invalid data")) }
            json
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
