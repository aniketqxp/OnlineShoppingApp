# VectorStore

[![Java Version](https://img.shields.io/badge/java-17%2B-orange)](https://www.oracle.com/java/)
[![Python Version](https://img.shields.io/badge/python-3.9%2B-blue)](https://www.python.org/)
[![Redis](https://img.shields.io/badge/redis-LATEST-red)](https://redis.io/)

VectorStore is a hybrid e-commerce system utilizing a Java Swing frontend and a Python-based asynchronous backend. The architecture implements distributed state management via Redis and semantic search capabilities via Pinecone vector indexing.

## Overview

The application is structured as a multi-tier system:
- **Frontend**: Java Swing desktop client managing user interaction and local UI state.
- **State Layer**: Redis cache instance for persistent session and cart management.
- **AI Microservice**: FastAPI server utilizing Sentence-Transformers for text vectorization.
- **Vector Database**: Pinecone serverless index for Approximate Nearest Neighbor (ANN) search.

## System Architecture

```mermaid
graph TD
    %% Node Definitions
    A[/User Interaction/] --> B{Java Frontend}
    B -->|State Sync| C[(Redis Persistence)]
    B -->|Search Query| D[FastAPI Microservice]
    
    subgraph AI_Engine [AI Search Pipeline]
        direction LR
        E([Sentence-Transformers])
        F[(Pinecone Vector Index)]
    end
    
    D -->|Text Mapping| E
    E -->|Vector Embeddings| F
    F -->|ANN Result| D
    
    D --> G>Semantic Search Results]
    G --> H[/Rendered UI View/]

    %% Styling
    style A fill:#2d3436,stroke:#000,color:#fff
    style B fill:#6c5ce7,stroke:#000,color:#fff
    style C fill:#fdcb6e,stroke:#000,color:#000
    style D fill:#0984e3,stroke:#000,color:#fff
    style E fill:#00b894,stroke:#000,color:#fff
    style F fill:#fdcb6e,stroke:#000,color:#000
    style G fill:#d63031,stroke:#000,color:#fff
    style H fill:#2d3436,stroke:#000,color:#fff
```

## Interface Gallery

| View | Screenshot |
| :--- | :--- |
| **Home** | ![Home](assets/homepage.png) |
| **Catalog** | ![Catalog](assets/categorypage.png) |
| **Cart** | ![Cart](assets/cartpage.png) |
| **Checkout** | ![Checkout](assets/checkoutpage.png) |

## Technical Specifications

### Core Dependencies
- **Java**: Swing, Jedis, Gson.
- **Python**: FastAPI, Uvicorn, Pinecone-client, Sentence-Transformers.
- **Infrastructure**: Redis (6.x+), Pinecone (Serverless).

### Deployment

#### 1. Python Environment Setup
```bash
pip install -r requirements.txt
```

#### 2. Vector Index Initialization
```bash
python ai-search-service/scripts/seed.py
```

#### 3. Execution
The application ecosystem is managed via a PowerShell orchestration script:
```powershell
.\run.ps1
```

*Note: The FastAPI microservice must be active on port 8000 for semantic search functionality.*
