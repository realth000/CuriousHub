package kzs.th000.curioushub.core.constants

/** Base url of all basic calls targeting github. */
const val BASE_URL = "https://github.com"

/** Base url of all api calls targeting github. */
const val API_BASE_URL = "https://api.github.com"

/** Domain part of the uri callback when auth get code and navigate back. */
const val AUTH_CODE_DOMAIN = "auth_code"

/** Uri callback when auth get code and navigate back. */
const val AUTH_CODE_URI = "kzs.th000.curioushub://$AUTH_CODE_DOMAIN"

/** Domain part of the uri callback when auth get user token and navigate back. */
const val AUTH_TOKEN_DOMAIN = "auth_token"

/** Uri callback when auth get user token and navigate back. */
const val AUTH_TOKEN_URI = "kzs.th000.curioushub://$AUTH_TOKEN_DOMAIN"
