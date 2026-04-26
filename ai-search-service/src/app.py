import os
from pathlib import Path
os.environ["USE_TF"] = "0"
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "3"
from fastapi import FastAPI, Query
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
from pinecone import Pinecone
from sentence_transformers import SentenceTransformer

load_dotenv(Path(__file__).parent.parent / ".env")

app = FastAPI(title="VectorStore AI Search")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["GET"],
    allow_headers=["*"],
)

# Configuration
PINECONE_API_KEY = os.getenv("PINECONE_API_KEY")
INDEX_NAME = os.getenv("PINECONE_INDEX_NAME", "shopwave-products")

# Initialize Pinecone and Model
pc = Pinecone(api_key=PINECONE_API_KEY)
index = pc.Index(INDEX_NAME)
model = SentenceTransformer('all-MiniLM-L6-v2')

@app.get("/search")
async def search(q: str = Query(..., min_length=1)):
    # 1. Vectorize query
    query_vec = model.encode(q).tolist()
    
    # 2. Query Pinecone
    results = index.query(vector=query_vec, top_k=6, include_metadata=False)
    
    # 3. Return matching product IDs (names) — SDK v3 returns objects
    product_ids = [match.id for match in results.matches]
    return {"results": product_ids}

@app.get("/health")
async def health():
    return {"status": "ok"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
