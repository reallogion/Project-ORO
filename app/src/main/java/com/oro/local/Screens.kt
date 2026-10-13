package com.oro.local
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oro.local.data.model.RiskLevel
import com.oro.local.data.model.RiskResult
import com.oro.local.data.model.VitalsSample
import com.oro.local.data.source.Scenario

@Composable
fun HomeScreen(
    sample: VitalsSample?,
    result: RiskResult?,
    scenario: Scenario,
    onScenario: (Scenario) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Simulator: $scenario")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Scenario.values().forEach { s ->
                Button(onClick = { onScenario(s) }) { Text(s.name) }
            }
        }

        if (result != null) {
            val color = when {
                result.learning -> Color(0xFFBBDEFB)
                result.level == RiskLevel.OK -> Color(0xFFC8E6C9)
                result.level == RiskLevel.WARNING -> Color(0xFFFFF59D)
                else -> Color(0xFFFFCDD2)
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = color),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (result.learning) "LEARNING" else result.level.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(result.explanation)
                    if (result.deviatingVitals.isNotEmpty()) {
                        Text("Deviating: " + result.deviatingVitals.joinToString(", "))
                    }
                }
            }
        }

        if (sample == null) {
            Text("Waiting for data...")
        } else {
            Text("Heart rate: ${sample.heartRate} bpm")
            Text("SpO2: ${sample.spo2} %")
            Text("Body temp: ${"%.1f".format(sample.bodyTempC)} °C")
            Text("Ambient: ${"%.1f".format(sample.ambientTempC)} °C, ${sample.humidityPct.toInt()} % humidity")
            Text("HRV: ${sample.hrvMs.toInt()} ms")
            Text("Pressure: ${sample.pressureHpa.toInt()} hPa")
            Text("Location: ${"%.4f".format(sample.latitude)}, ${"%.4f".format(sample.longitude)}")
            Text("Battery: ${sample.batteryPct} %")
        }
    }
}

@Composable
fun RisksScreen(result: RiskResult?) {
    Placeholder(
        "Risk categories",
        "Coming next: heat stress, dehydration, respiratory, cardiovascular, fatigue and fall scores.\n\nCurrent overall status: ${result?.level ?: "..."}"
    )
}

@Composable
fun HistoryScreen() {
    Placeholder("History", "Coming: trend charts and daily summaries, stored on this phone.")
}

@Composable
fun SettingsScreen() {
    var broadcast by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Switch(checked = broadcast, onCheckedChange = { broadcast = it })
            Text("Broadcast to coordinator (watch setting, mirrored here)")
        }
        Text("Emergency contacts, device pairing and Site Key will go here.")
    }
}

@Composable
fun SurveillanceScreen() {
    Placeholder(
        "Surveillance mode",
        "Coming: list of enrolled watches (Site Key scoped), live and last-seen status, tap to inspect one wearer."
    )
}

@Composable
private fun Placeholder(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        Text(body)
    }
}