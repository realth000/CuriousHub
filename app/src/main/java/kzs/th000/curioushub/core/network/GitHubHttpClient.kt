package kzs.th000.curioushub.core.network

import android.util.Log
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.ktor.http.ContentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kzs.th000.curioushub.BuildConfig
import kzs.th000.curioushub.core.exceptions.AppEither
import kzs.th000.curioushub.core.exceptions.AppException
import kzs.th000.curioushub.core.models.AuthTokenSuccessModel
import kzs.th000.curioushub.core.models.UserProfileModel

abstract class GhAccept {
    companion object {
        val VndJson: ContentType = ContentType("application", "vnd.github+json")
    }
}

class GitHubHttpClient {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
            isLenient = true
        }

        /**
         * Try deserialize [data] into [T] type.
         *
         * @param data The [JsonObject] to decode.
         * @return T if deserialization succeeded.
         */
        private inline fun <reified T> Json.decodeObjectOrRaise(data: JsonObject): AppEither<T> =
            either {
                catch({ json.decodeFromJsonElement<T>(data) }) { e ->
                    raise(
                        AppException.SerializationFailure(
                            "failed to deserialize ${T::class}: ${e.message}"
                        )
                    )
                }
            }

        suspend fun getAuthToken(
            clientId: String,
            clientSecret: String,
            code: String,
            redirectUri: String,
        ): AppEither<AuthTokenSuccessModel> = either {
            val target =
                AppHttpClient.buildGhTarget(
                    paths = listOf("login", "oauth", "access_token"),
                    queryParameters =
                        listOf(
                            AppUriParam("client_id", clientId),
                            AppUriParam("client_secret", clientSecret),
                            AppUriParam("code", code),
                            AppUriParam("redirect_uri", redirectUri),
                        ),
                )

            val resp = AppHttpClient().postJson(target).bind()

            ensure(!resp.keys.contains("error") && !resp.keys.contains("error_description")) {
                val error = resp["error_description"]?.toString()
                Log.e("GithubHttpClient::getAuthToken", "error found in resp: $error")
                raise(
                    AppException.HttpServerRespondedAnError(
                        error = "failed to get auth token: $error"
                    )
                )
            }

            json.decodeObjectOrRaise<AuthTokenSuccessModel>(resp).bind()
        }

        suspend fun getCurrentUserInfo(accessToken: String): AppEither<UserProfileModel> = either {
            val target = AppHttpClient.buildGhApiTarget(paths = listOf("user"))

            val data =
                AppHttpClient()
                    .getJson(
                        target,
                        accept = GhAccept.VndJson,
                        headers = listOf(AppUriParam("X-GitHub-Api-Version", "2022-11-28")),
                        authorization = "Bearer $accessToken",
                    )
                    .bind()

            json.decodeObjectOrRaise<UserProfileModel>(data).bind()
        }

        suspend fun refreshAccessToken(refreshToken: String): AppEither<AuthTokenSuccessModel> =
            either {
                val target =
                    AppHttpClient.buildGhTarget(
                        paths = listOf("login", "oauth", "access_token"),
                        queryParameters =
                            listOf(
                                AppUriParam("client_id", BuildConfig.CLIENT_ID),
                                AppUriParam("client_secret", BuildConfig.CLIENT_SECRET),
                                AppUriParam("grant_type", "refresh_token"),
                                AppUriParam("refresh_token", refreshToken),
                            ),
                    )

                val resp = AppHttpClient().postJson(target).bind()

                ensure(!resp.keys.contains("error") && !resp.keys.contains("error_description")) {
                    val error = resp["error_description"]?.toString()
                    Log.e("GithubHttpClient::refreshAccessToken", "error found in resp: $error")
                    raise(
                        AppException.HttpServerRespondedAnError(
                            error = "failed to get auth token: $error"
                        )
                    )
                }

                json.decodeObjectOrRaise<AuthTokenSuccessModel>(resp).bind()
            }
    }
}
