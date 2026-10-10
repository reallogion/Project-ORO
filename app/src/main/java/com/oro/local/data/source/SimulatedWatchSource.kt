package com.oro.local.data.source

import com.oro.local.data.model.VitalsSample
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.roundToInt
import kotlin.random.Random

enum class Scenario { NORMAL, HEATWAVE, FEVER }

class SimulatedWatchSource : WatchDataSource {

    @Volatile
    var scenario: Scenario = Scenario.NORMAL

    private var battery = 100.0

    override fun samples(): Flow<VitalsSample> = flow {
        while (true) {
            emit(generate())
            delay(1000)
        }
    }

    private fun generate(): VitalsSample {
        var hr = 72.0
        var spo2 = 98.0
        var body = 36.6
        var ambient = 28.0
        var humidity = 50.0
        var hrv = 55.0

        when (scenario) {
            Scenario.NORMAL -> {}
            Scenario.HEATWAVE -> {
                ambient = 42.0; humidity = 70.0
                body = 37.4; hr = 95.0; hrv = 40.0
            }
            Scenario.FEVER -> {
                body = 38.6; hr = 98.0; spo2 = 96.0; hrv = 35.0
            }
        }

        battery = (battery - 0.01).coerceAtLeast(0.0)

        return VitalsSample(
            timestampMs = System.currentTimeMillis(),
            heartRate = (hr + Random.nextDouble(-3.0, 3.0)).roundToInt(),
            spo2 = (spo2 + Random.nextDouble(-1.0, 1.0)).roundToInt(),
            hrvMs = (hrv + Random.nextDouble(-4.0, 4.0)).toFloat(),
            bodyTempC = (body + Random.nextDouble(-0.1, 0.1)).toFloat(),
            ambientTempC = (ambient + Random.nextDouble(-0.5, 0.5)).toFloat(),
            humidityPct = (humidity + Random.nextDouble(-2.0, 2.0)).toFloat(),
            pressureHpa = (1013 + Random.nextDouble(-1.0, 1.0)).toFloat(),
            accelX = Random.nextDouble(-0.05, 0.05).toFloat(),
            accelY = Random.nextDouble(-0.05, 0.05).toFloat(),
            accelZ = (1.0 + Random.nextDouble(-0.05, 0.05)).toFloat(),
            gyroX = Random.nextDouble(-2.0, 2.0).toFloat(),
            gyroY = Random.nextDouble(-2.0, 2.0).toFloat(),
            gyroZ = Random.nextDouble(-2.0, 2.0).toFloat(),
            activityLevel = 0,
            latitude = 28.6139,
            longitude = 77.2090,
            gpsFix = true,
            sosActive = false,
            batteryPct = battery.roundToInt()
        )
    }
}