# MouseFlow | Systems Telemetry Utility

A high-performance Java utility engineered to intercept and normalize global mouse telemetry at the Operating System level. Built using JNA (Java Native Access) to interface with the Win32 API, this utility handles high-frequency data ingestion with a focus on resource optimization and concurrency.

##  Technical Architecture
- **Low-Level Interception:** Utilizes `User32.dll` and `SetWindowsHookEx` (Low-level Mouse Hook) via JNA to capture telemetry outside of the application's focused window.
- **Concurrency Model:** Implemented a decoupled logging architecture using `ExecutorService` to ensure mouse polling remains non-blocking, preventing UI lag or system-level latency.
- **Data Persistence:** Optimized logging via a weighted-pulse strategy to manage I/O overhead during high-frequency movement.

##  Engineering Challenges
### 1. Multi-Monitor Coordinate Normalization
*Challenge:* Windows OS returns coordinates relative to the primary monitor, which creates "Dead zones" or negative values in staggered multi-monitor setups.  
*Solution:* Developed custom transformation logic to normalize coordinates across a virtual desktop space, ensuring 1:1 data accuracy for heat-map generation.

### 2. Thread Safety & OS Hooks
*Challenge:* Intercepting OS-level hooks can lead to system instability if the message loop is blocked.  
*Solution:* Isolated the Win32 message loop on a dedicated thread with a high-priority interrupt handler, ensuring the OS hook is released within the required millisecond window.

##  Performance Metrics
- **CPU Overhead:** < 1% during active telemetry ingestion.
- **Latency:** Sub-millisecond interrupt handling.
- **Data Integrity:** 100% capture rate across 4K display resolutions.

##  Project Roadmap (Active Development)
This utility is under active weekly development. Current focus areas include:

- [x] **Phase I: Core Ingestion** - Low-level Win32 hook implementation and coordinate normalization. (Current State)
- [ ] **Phase II: Persistence Layer** - Transitioning from raw pulse-logging to a local SQLite instance for structured telemetry storage.
- [ ] **Phase III: Analytics Engine** - Developing a mathematical model to calculate "Distance Traveled" and "Efficiency Heuristics" across different application contexts.
- [ ] **Phase IV: Visualization** - Implementing a JavaFX-based dashboard for real-time heat-map rendering.
