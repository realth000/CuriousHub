package kzs.th000.curioushub.core.network

import android.util.Log
import io.ktor.http.ContentType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
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

        suspend fun getAuthToken(
            clientId: String,
            clientSecret: String,
            code: String,
            redirectUri: String,
        ): AuthTokenSuccessModel? {
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

            Log.d("GithubHttpClient::getAuthToken", ">>> request token from $target")
            val resp = AppHttpClient().postJson(target)

            if (resp == null) {
                Log.i("LoginViewModel", ">>> request token: null result")
                return null
            }

            if (resp.keys.contains("error") && resp.keys.contains("error_description")) {
                Log.d(".MainActivity", ">>> request token: error found: ${resp.getValue("error")}")
                return null
            }

            return json.decodeFromJsonElement<AuthTokenSuccessModel>(resp)
        }

        suspend fun getCurrentUserInfo(accessToken: String): UserProfileModel? {
            val target = AppHttpClient.buildGhApiTarget(paths = listOf("user"))

            return AppHttpClient()
                .getJson(
                    target,
                    accept = GhAccept.VndJson,
                    headers = listOf(AppUriParam("X-GitHub-Api-Version", "2022-11-28")),
                    authorization = "Bearer $accessToken",
                )
                ?.let { json.decodeFromJsonElement<UserProfileModel>(it) }
        }
    }
}
