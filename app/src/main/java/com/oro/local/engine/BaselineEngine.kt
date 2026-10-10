package com.oro.local.engine

import com.oro.local.data.model.RiskLevel
import com.oro.local.data.model.RiskResult
import com.oro.local.data.model.VitalsSample
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/** One baseline (EWMA) + one CUSUM change detector for a single vital. */
class VitalTracker(private val stdFloor: Double) {
    var mean = 0.0
        private set
    var cusum = 0.0
        private set
    private var count = 0

    val fired: Boolean get() = cusum > H

    fun update(x: Double, learning: Boolean) {
        if (learning) {
            count++
            mean += (x - mean) / count
            return
        }
        val z = abs(x - mean) / stdFloor
        cusum = min(CAP, max(0.0, cusum + z - K))
        // Baseline only keeps learning while things look normal (frozen otherwise)
        if (cusum == 0.0) mean += ALPHA * (x - mean)
    }

    companion object {
        const val K = 1.0      // ignore deviations smaller than this many "std"
        const val H = 6.0      // alarm threshold
        const val CAP = 12.0   // so recovery doesn't take forever
        const val ALPHA = 0.01 // baseline learning speed
    }
}

class BaselineEngine {
    private val hr = VitalTracker(4.0)
    private val spo2 = VitalTracker(1.5)
    private val temp = VitalTracker(0.3)
    private val hrv = VitalTracker(6.0)
    private val ambient = VitalTracker(2.0) // referee signal only
    private var n = 0

    fun update(s: VitalsSample): RiskResult {
        n++
        val learning = n <= WARMUP

        hr.update(s.heartRate.toDouble(), learning)
        spo2.update(s.spo2.toDouble(), learning)
        temp.update(s.bodyTempC.toDouble(), learning)
        hrv.update(s.hrvMs.toDouble(), learning)
        ambient.update(s.ambientTempC.toDouble(), learning)

        if (learning) {
            return RiskResult(
                RiskLevel.OK, true,
                "Learning your baseline ($n/$WARMUP)... keep NORMAL selected",
                emptyList()
            )
        }

        val fired = buildList {
            if (hr.fired) add("Heart rate")
            if (spo2.fired) add("SpO2")
            if (temp.fired) add("Body temp")
            if (hrv.fired) add("HRV")
        }

        // Layer B result
        var level = when {
            fired.size >= 3 -> RiskLevel.CRITICAL
            fired.isNotEmpty() -> RiskLevel.WARNING
            else -> RiskLevel.OK
        }

        // Layer A: generic safety limits (work for anyone, from second one)
        if (s.spo2 < 88 || s.bodyTempC >= 39.5f || s.bodyTempC <= 35.0f || s.heartRate > 140) {
            level = RiskLevel.CRITICAL
        } else if (level == RiskLevel.OK && (s.spo2 < 92 || s.bodyTempC >= 38.0f)) {
            level = RiskLevel.WARNING
        }

        val explanation = when {
            fired.isEmpty() -> "All vitals are within your personal baseline."
            ambient.fired -> "Surroundings changed too: likely an environmental hazard (e.g. heatwave). Baseline frozen."
            else -> "Only your body changed, surroundings normal: possible health issue. Baseline frozen."
        }

        return RiskResult(level, false, explanation, fired)
    }

    companion object {
        const val WARMUP = 10
    }
}