package kzs.th000.curioushub.core.models

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
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
        val currentUsername =
            AppSettingsValue<String>(
                "currentUsername",
                value = stringPreferencesKey("currentUsername"),
                defaultValue = "",
            )
        val currentUid =
            AppSettingsValue<Int>(
                "currentUid",
                value = intPreferencesKey("currentUid"),
                defaultValue = -1,
            )
    }
}
