# 🚀 OmniRoute for Android — The Free AI Gateway & Router

A native Android application written in Kotlin and Jetpack Compose that ports the full power of **OmniRoute** — the unified AI gateway and multi-model router.

---

## 🌟 Key Features

1. **AI Gateway & Free Budget Dashboard**
   - Live Free Tokens Meter tracking **~1.53 Billion free tokens / month** across **43 provider pools** and **500+ models**.
   - Real-time Gateway Health, active connections, circuit breakers, and RTK token savings counters.
   - Live breakdown for individual pools (Mistral Large 3 1B pool, OpenAI GPT-4o mini 150M pool, Google Gemini 2.5 Flash 60M pool, Cerebras wafer inference, DeepSeek R1).

2. **291 AI Providers Catalog & Key Management**
   - Searchable catalog of 291 AI providers grouped into:
     - **Free Tiers**: Google Gemini Flash, DeepSeek, Mistral, Cerebras, OpenRouter, SiliconFlow.
     - **Commercial Leaders**: OpenAI (GPT-4o, o3-mini), Anthropic Claude (3.7 Sonnet, Haiku), Google Vertex AI.
     - **Ultra-Fast Inference**: Cerebras CS-3 (1,200+ tok/s), Groq LPU (500+ tok/s), SambaNova.
     - **Local / Self-Hosted**: Ollama, LM Studio, vLLM.
   - In-app API key encryption & storage with live latency ping testing.

3. **19 Advanced Routing Strategies & Combos**
   - Supports 19 strategies: Auto-Combo (13-factor scoring), Strict Priority, Weighted Round Robin, Round Robin, Least Used, Cost Optimized, Reset-Aware RPM, Context-Optimized, Cache-Optimized, Context-Relay, Fusion (Judge Panel), Pipeline, P2C, Headroom, Fill-First, and LKGP.
   - Create custom combos with fallback sequence chains and failover error codes (429, 500, 502, 503, 504).

4. **Interactive Playground & Chat**
   - Real-time chat with streaming response simulation.
   - **RTK + Caveman Stacked Compression Engine**: Cuts prompt bloat, redundant whitespaces, and conversational filler to save 15–95% tokens (~89% average).
   - **Transparent 429 Failover Simulator**: Toggle "Simulate 429 Rate Limit" to watch the router instantly transparently fail over to secondary providers in <15ms without dropping the user's connection.
   - Real-time metrics per message: Target Model, Dispatched Provider, Time-To-First-Token (TTFT), throughput (tokens/sec), and token savings.

5. **Traffic & Telemetry Inspector**
   - Real-time request log stream.
   - Detailed inspection trace: Endpoint, Status code (200 OK, 429 Failover, 503 Retried), Upstream Latency, Prompt/Completion Tokens, Circuit Breaker snapshot, and Client Agent headers.

---

## 🛠️ Architecture & Tech Stack

- **Target OS**: Android (API 26+)
- **Language**: Kotlin 2.2
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Theme**: Dark cyber aesthetic (OmniBg `#0B0F19`, OmniCyan `#00F0FF`, OmniPurple `#A855F7`, OmniGreen `#10B981`)
- **Adaptive Launcher**: Custom Material adaptive icon with generated glowing neural router artwork
- **Build System**: Gradle 9.3.1 with Android Gradle Plugin 9.1.1

---

## 📱 Build & Run

Run the standard debug build via Gradle:

```bash
gradle assembleDebug
```
The output APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`
