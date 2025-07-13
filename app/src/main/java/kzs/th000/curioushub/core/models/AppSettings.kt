package kzs.th000.curioushub.core.models

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

data class AppSettingsValue<T>(
    val key: String,
    val value: Preferences.Key<T>,
    private val defaultValue: T,
) {
    fun update(pref: MutablePreferences, value: T) {
        pref[this.value] = value
    }

    fun getOrNull(pref: Preferences): T? = pref[this.value]

    fun get(pref: Preferences): T? = pref[this.value] ?: defaultValue
}

abstract class AppSettings() {
    companion object {
        val currentUser =
            AppSettingsValue<String>(
                "currentUser",
                value = stringPreferencesKey("currentUser"),
                defaultValue = "",
            )
    }
}
