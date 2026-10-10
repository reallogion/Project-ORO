package com.oro.local

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.oro.local.data.model.RiskLevel
import com.oro.local.data.model.RiskResult
import com.oro.local.data.model.VitalsSample
import com.oro.local.data.source.Scenario
import com.oro.local.data.source.SimulatedWatchSource
import com.oro.local.engine.BaselineEngine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LiveVitalsScreen()
                }
            }
        }
    }
}

@Composable
fun LiveVitalsScreen() {
    val source = remember { SimulatedWatchSource() }
    val engine = remember { BaselineEngine() }
    var sample by remember { mutableStateOf<VitalsSample?>(null) }
    var result by remember { mutableStateOf<RiskResult?>(null) }
    var current by remember { mutableStateOf(Scenario.NORMAL) }

    LaunchedEffect(Unit) {
        source.samples().collect { s ->
            sample = s
            result = engine.update(s)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("ORO Local - Live Vitals", style = MaterialTheme.typography.headlineSmall)
        Text("Scenario: $current")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Scenario.values().forEach { s ->
                Button(onClick = {
                    source.scenario = s
                    current = s
                }) { Text(s.name) }
            }
        }

        val r = result
        if (r != null) {
            val color = when {
                r.learning -> Color(0xFFBBDEFB)
                r.level == RiskLevel.OK -> Color(0xFFC8E6C9)
                r.level == RiskLevel.WARNING -> Color(0xFFFFF59D)
                else -> Color(0xFFFFCDD2)
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = color),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (r.learning) "LEARNING" else r.level.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(r.explanation)
                    if (r.deviatingVitals.isNotEmpty()) {
                        Text("Deviating: " + r.deviatingVitals.joinToString(", "))
                    }
                }
            }
        }

        val v = sample
        if (v == null) {
            Text("Waiting for data...")
        } else {
            Text("Heart rate: ${v.heartRate} bpm")
            Text("SpO2: ${v.spo2} %")
            Text("Body temp: ${"%.1f".format(v.bodyTempC)} °C")
            Text("Ambient: ${"%.1f".format(v.ambientTempC)} °C, ${v.humidityPct.toInt()} % humidity")
            Text("HRV: ${v.hrvMs.toInt()} ms")
            Text("Battery: ${v.batteryPct} %")
        }
    }
}