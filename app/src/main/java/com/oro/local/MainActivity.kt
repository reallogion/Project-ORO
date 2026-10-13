package com.oro.local

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                    OroApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OroApp() {
    val source = remember { SimulatedWatchSource() }
    val engine = remember { BaselineEngine() }
    var sample by remember { mutableStateOf<VitalsSample?>(null) }
    var result by remember { mutableStateOf<RiskResult?>(null) }
    var scenario by remember { mutableStateOf(Scenario.NORMAL) }
    var surveillanceMode by remember { mutableStateOf(false) }
    var tab by remember { mutableStateOf(0) }
    val tabs = listOf("Home", "Risks", "History", "Settings")

    LaunchedEffect(Unit) {
        source.samples().collect { s ->
            sample = s
            result = engine.update(s)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (surveillanceMode) "ORO Surveillance" else "ORO Local") },
                actions = {
                    TextButton(onClick = { surveillanceMode = !surveillanceMode }) {
                        Text(if (surveillanceMode) "Personal mode" else "Surveillance mode")
                    }
                }
            )
        },
        bottomBar = {
            if (!surveillanceMode) {
                NavigationBar {
                    tabs.forEachIndexed { i, name ->
                        NavigationBarItem(
                            selected = tab == i,
                            onClick = { tab = i },
                            icon = {},
                            label = { Text(name) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (surveillanceMode) {
                SurveillanceScreen()
            } else {
                when (tab) {
                    0 -> HomeScreen(sample, result, scenario) {
                        source.scenario = it
                        scenario = it
                    }
                    1 -> RisksScreen(result)
                    2 -> HistoryScreen()
                    else -> SettingsScreen()
                }
            }
        }
    }
}