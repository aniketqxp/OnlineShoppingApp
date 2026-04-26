# VectorStore: Technical Implementation Manual

This document provides an exhaustive forensic audit of the engineering decisions, algorithmic implementations, and architectural workarounds utilized in the VectorStore system.

## 1. Logic & Algorithms

### 1.1 Semantic Retrieval Pipeline
The core search functionality transitions from a O(n) linear string-matching algorithm to an asynchronous semantic retrieval pipeline:
- **Vectorization**: User queries are intercepted by the FastAPI microservice and transformed into 384-dimensional dense vectors using the `all-MiniLM-L6-v2` Sentence-Transformer model.
- **Vector Indexing**: High-dimensional vectors are queried against a Pinecone serverless index using Cosine Similarity. This allows for conceptual retrieval where query terms do not explicitly appear in product metadata.
- **Java-Side Parsing**: Since the Java client lacks a native JSON schema parser, the `SearchResultsPage.java` implements a custom regex-based tokenizer (`json.replaceAll(".*\\[(.*)\\].*", "$1")`) to extract product identifiers from the HTTP response body while maintaining zero external dependencies for JSON processing.

### 1.2 State Synchronization & Fallback
The `AppState.java` implements a tiered persistence logic:
- **Primary**: Redis Hash (`HSET`) operations are used for cart state, keyed by a persistent UUID session ID stored in the user's home directory (`.vectorstore_session`).
- **Fallback**: A graceful degradation path is implemented via a `JedisPool` health check. If the Redis socket connection fails or times out (5000ms), the system silently diverts all state mutations to a local `LinkedHashMap` to prevent application crash in offline environments.

---

## 2. Engineering "Fires" & Workarounds

### 2.1 Keras 3 / Transformers Backend Conflict
During the deployment of the AI service, a critical dependency conflict was identified where `transformers` attempted to load a TensorFlow backend incompatible with Keras 3 on Windows.
- **Resolution**: Forced the environment variable `USE_TF=0` at the entry point of `app.py` and `seed.py`. This suppressed the TensorFlow initialization and forced the `sentence-transformers` library to utilize the PyTorch backend, resolving the runtime `ValueError`.

### 2.2 Windows Character Encoding
Character corruption was observed in the UI (specifically the copyright Unicode symbol) due to default Windows-1252 encoding in the `javac` compiler.
- **Resolution**: Updated the orchestration script `run.ps1` to force `-encoding UTF-8` during the compilation phase, ensuring consistent font rendering across all OS environments.

### 2.3 Headless Redis Detection
The `run.ps1` script implements a "pre-flight" check for the Redis binary. It utilizes `redis-cli ping` to verify existing instances before attempting a background process start. If starting, it uses `Start-Process -WindowStyle Minimized` to prevent terminal hijacking during the Java application launch.

---

## 3. Performance & Scaling

### 3.1 Concurrency & UI Threading
To prevent UI freezing during blocking I/O (Redis network calls and REST API requests), the application strictly adheres to the **Event Dispatch Thread (EDT)** pattern:
- All state changes trigger a `fire()` event, which uses `SwingUtilities.invokeLater()` to synchronize the UI components.
- Network timeouts are explicitly set (`1500ms` connect / `3000ms` read) in `SearchResultsPage.java` to ensure the search UI remains responsive even if the AI microservice experiences high latency.

### 3.2 Memory Optimization (Image Caching)
The `ImageCache.java` implements a singleton pattern with a `HashMap<String, BufferedImage>` to prevent redundant disk I/O.
- **Scalable Rendering**: Images are scaled using `Image.SCALE_SMOOTH` during the first retrieval and cached in their raw format to allow for varied resolution requirements across the `ProductCard` and `ProductDetail` views without repeated scaling computation.

---

## 4. Architecture & Domain Nuance

### 4.1 Hybrid Microservice Interop
The system follows a "Sidecar" architecture where the Python AI node acts as a specialized search engine for the Java monolith. 
- **Decoupling**: The Java frontend is completely unaware of the AI logic (embeddings, vector dimensions, etc.); it only consumes a standardized JSON list of product names, which are then resolved against the local `ProductCatalog`.

### 4.2 UI/Theme Decoupling
Visual logic is completely abstracted into `Theme.java` and `UIUtils.java`. 
- **Consistency**: All UI components utilize centralized constants for spacing, typography, and color tokens. This ensures that a global brand change (e.g., the rename to VectorStore) can be executed by modifying a single `Theme` class rather than auditing every view.
