package com.oro.local.data.model

data class VitalsSample(
    val timestampMs: Long,

    // MAX30102
    val heartRate: Int,
    val spo2: Int,
    val hrvMs: Float,

    // MLX90614
    val bodyTempC: Float,

    // BME280
    val ambientTempC: Float,
    val humidityPct: Float,
    val pressureHpa: Float = 1013f,

    // MPU6050 (accel in g, gyro in deg/s)
    val accelX: Float = 0f,
    val accelY: Float = 0f,
    val accelZ: Float = 1f,
    val gyroX: Float = 0f,
    val gyroY: Float = 0f,
    val gyroZ: Float = 0f,
    val activityLevel: Int = 0, // 0 rest, 1 light, 2 moderate, 3 intense

    // NEO-6M
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val gpsFix: Boolean = false,

    // Device
    val sosActive: Boolean = false,
    val batteryPct: Int = 100
)