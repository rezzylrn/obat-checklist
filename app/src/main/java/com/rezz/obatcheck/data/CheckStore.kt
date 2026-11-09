package com.rezz.obatcheck.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.checkDataStore: DataStore<Preferences> by preferencesDataStore(name = "obat_check")

/**
 * Status centang tiap obat, tersimpan permanen di DataStore Preferences.
 * Key: "checked_<id obat>" -> true. Obat yang belum pernah dicentang tidak punya key.
 */
class CheckStore(private val context: Context) {

    val checkedIds: Flow<Set<String>> = context.checkDataStore.data.map { prefs ->
        prefs.asMap().entries
            .filter { (key, value) -> value == true && key.name.startsWith("checked_") }
            .map { (key, _) -> key.name.removePrefix("checked_") }
            .toSet()
    }

    suspend fun setChecked(id: String, checked: Boolean) {
        context.checkDataStore.edit { prefs ->
            prefs[booleanPreferencesKey("checked_$id")] = checked
        }
    }

    suspend fun resetAll() {
        context.checkDataStore.edit { prefs -> prefs.clear() }
    }
}
