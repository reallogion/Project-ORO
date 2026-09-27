# ORO — Observe · Respond · Outreach

A privacy-preserving personal health companion for disaster resilience.
Built for **Smart India Hackathon 2026 — Problem Statement 26181** (Personal Health Companion).

> **Status: Work in progress.** Sensor setup and basic testing (HR/SpO2 via MAX30102) are done. Full sensor suite, trained AI models, and the companion app are in active development. This repo will be updated incrementally over the next few days — see [Project Status](#project-status) below.

---

## The Problem

During disasters — heatwaves, floods, cyclones, air pollution events — health decline often goes unnoticed until it's critical, exactly when networks and hospitals are least reachable. Existing wearables use fixed thresholds that can't tell a real emergency apart from normal disaster-stress, and rural elderly, outdoor workers, and chronic-condition patients — the most vulnerable — are the least monitored.

## What ORO Is

ORO is a wristwatch that runs **personal-baseline AI fully on-device** — no internet, no cloud, no infrastructure required. It learns the wearer's own normal vitals instead of using generic population thresholds, so it can tell a real deviation apart from ordinary stress, exertion, or travel.

An optional Android companion app, **ORO Local**, syncs over BLE to add a richer dashboard, disaster-forecast caching, SMS-based SOS, and a consent-based Surveillance Mode for institutional deployment — but the watch is never dependent on it.

## Core Capabilities

- **Continuous monitoring** — heart rate, SpO2, body temperature, motion/fall, ambient temp/humidity, air quality
- **Two-layer on-device AI:**
  - *Layer A* — pretrained generic classifier (fall/distress patterns), works from day one
  - *Layer B* — personal baseline system (bucketed EWMA + CUSUM), personalizes over time, GPS-gated to disambiguate fever vs. heatwave vs. travel
- **Auto-detected disaster context** — heatwave / pollution risk inferred from sensor fusion, no manual setup
- **Layered emergency response** — local alarm → black-box SD log → watch-to-watch peer BLE alert (no phone needed) → phone SMS relay (if paired)
- **Consent-based Surveillance Mode** — same watch, no new hardware, for institutional/disaster-response deployment

## Hardware

| Component | Purpose |
|---|---|
| ESP32-S3 Super Mini | MCU — sensor logic, on-device AI, BLE |
| MAX30102 | Heart rate + SpO2 |
| MLX90614 | IR body temperature |
| BME280 | Ambient temperature, humidity, pressure |
| MPU6050 | Motion / fall detection |
| SGP41 | Air quality |
| NEO-6M | GPS |
| 1.8" SPI display + SD card | On-watch dashboard + local logging |

## Software / Tech Stack

- **Firmware:** Arduino / ESP-IDF
- **Companion app:** Kotlin (Android)
- **Communication:** BLE
- **Local storage:** SD card (watch), SQLite (app)
- **AI/ML:** trained on [SisFall](https://pmc.ncbi.nlm.nih.gov/articles/PMC5298771), [WESAD](https://archive.ics.uci.edu/dataset/465/wesad), [PPG-DaLiA](https://archive.ics.uci.edu/dataset/495/ppg+dalia)

## Project Status

- [x] Sensor bring-up and I2C/UART testing (HR/SpO2 working; remaining sensors in progress)
- [ ] Full two-layer AI pipeline on-device
- [ ] SD card logging (rolling log, baseline stats, black-box event log)
- [ ] BLE sync to ORO Local
- [ ] ORO Local Android app (dashboard, SOS, Surveillance Mode)
- [ ] Full standalone demo

## References

See [full reference list](#) *(link to your references doc/slide once finalized)* — includes sensor datasheets, training datasets, and related research on edge-AI wearable health monitoring.

## License

*(add your license here — MIT is a common default for hackathon projects)*
