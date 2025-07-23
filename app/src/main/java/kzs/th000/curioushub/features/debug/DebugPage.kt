package kzs.th000.curioushub.features.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.launch
import kzs.th000.curioushub.data.database.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugPage(db: AppDatabase, pref: DataStore<Preferences>) {

    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("Debug Page") }) }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = { db.clearAllTables() }) { Text("Clear Database") }
            Button(onClick = { coroutineScope.launch { pref.edit { it.clear() } } }) {
                Text("Clear DataStore")
            }
        }
    }
}
