# MouseFlow Tracker (Java)

A high-frequency system utility designed to capture, persist, and analyze real-time mouse behavior and application focus across multi-monitor environments.

## 🚀 Technical Highlights
- **Native OS Integration:** Leverages **JNativeHook** and **JNA (Java Native Access)** to intercept global mouse events and query the Windows `User32` API for active window handles (`HWND`).
- **State-Weighted Pulse Logic:** Implements a 40ms heartbeat timer that accumulates duration at a single coordinate, significantly compressing data volume without losing "dwell time" accuracy.
- **Context-Aware Tracking:** Simultaneously logs the **Foreground Application** (User Focus) and the **Window Under Cursor** (Hover Focus) for behavioral analysis.
- **Multi-Monitor Coordinate Scaling:** Engineered to handle non-rectangular virtual desktops (e.g., staggered 1440p + 1080p setups) by calculating global bounding boxes and normalizing coordinates for visualization.

## 🛠️ Tech Stack
- **Language:** Java 21
- **UI Framework:** JavaFX (with CSS-based Glassmorphism design)
- **Native Bridge:** JNA (Win32 API), JNativeHook
- **Architecture:** Decoupled Event-Capture (Asynchronous) and Data-Persistence (Buffered CSV) layers.

## 📈 Roadmap
- [ ] **Canvas Visualization:** Real-time path preview using JavaFX GraphicsContext.
- [ ] **SQLite Migration:** Transition from CSV to indexed SQLite for sub-second query performance on large datasets.
- [ ] **Heatmap Engine:** Generate static and time-lapse heatmaps using coordinate density algorithms.
