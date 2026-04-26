import os
from pathlib import Path
os.environ["USE_TF"] = "0"
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "3"
from dotenv import load_dotenv
from pinecone import Pinecone, ServerlessSpec
from sentence_transformers import SentenceTransformer

load_dotenv(Path(__file__).parent.parent / ".env")

# Configuration
PINECONE_API_KEY = os.getenv("PINECONE_API_KEY")
INDEX_NAME = os.getenv("PINECONE_INDEX_NAME", "shopwave-products")

# Product Data extracted from Java
PRODUCTS = [
    {"id": "Phone", "text": "Phone Sleek smartphone with 6.5\" OLED display, 128 GB storage, and a 12 MP dual camera. Electronics"},
    {"id": "Tablet", "text": "Tablet 10\" HD tablet perfect for work and entertainment. 64 GB with expandable storage. Electronics"},
    {"id": "Laptop", "text": "Laptop Lightweight 14\" laptop - Intel i5, 16 GB RAM, 512 GB SSD, all-day battery life. Electronics"},
    {"id": "Camera", "text": "Camera 24 MP mirrorless camera with interchangeable lenses and 4K video recording. Electronics"},
    {"id": "TV", "text": "TV 55\" 4K Smart TV with HDR, built-in streaming apps, and hands-free voice control. Electronics"},
    {"id": "Console", "text": "Console Next-gen gaming console - 1 TB storage, 4K gaming, and full online multiplayer. Electronics"},
    {"id": "Notebook", "text": "Notebook A5 hardcover lined notebook, 200 pages. Ideal for notes, journaling, and planning. Stationery"},
    {"id": "Pen", "text": "Pen Smooth ballpoint pen with blue ink and an ergonomic grip for comfortable writing. Stationery"},
    {"id": "Pencil", "text": "Pencil Pre-sharpened HB pencil. Ideal for sketching, drawing, and everyday writing. Stationery"},
    {"id": "Eraser", "text": "Eraser Clean-erasing white rubber eraser. No smudges. Compatible with all pencil types. Stationery"},
    {"id": "Marker", "text": "Marker Chisel-tip permanent marker. Waterproof, fade-resistant vibrant black ink. Stationery"},
    {"id": "Sharpener", "text": "Sharpener Dual-hole metal sharpener with a precision blade for standard and jumbo pencils. Stationery"},
    {"id": "Hat", "text": "Hat Classic 6-panel cotton cap with an adjustable strap. One size fits most. Accessories"},
    {"id": "Sunglasses", "text": "Sunglasses UV400 polarized sunglasses. Lightweight frame, unisex style. Full eye protection. Accessories"},
    {"id": "Watch", "text": "Watch Analog quartz watch with a stainless steel case. Water-resistant up to 30 m. Accessories"},
    {"id": "Gloves", "text": "Gloves Touchscreen-compatible knit gloves. Warm, stretchy, and snug fit for cold weather. Accessories"},
    {"id": "Backpack", "text": "Backpack 20 L backpack with a padded laptop sleeve, multiple pockets, and a built-in USB port. Accessories"},
    {"id": "Scarf", "text": "Scarf Soft knit winter scarf, 180 cm long. Neutral tones that pair with any outfit. Accessories"},
    {"id": "Football", "text": "Football Official size 5 football. Durable PU leather, machine-stitched for consistency. Sports"},
    {"id": "Basketball", "text": "Basketball Official size 7 basketball. Deep channel design for superior grip and ball control. Sports"},
    {"id": "Baseball", "text": "Baseball Official weight and size baseball - cork centre, wool winding, cowhide cover. Sports"},
    {"id": "Cricket Bat", "text": "Cricket Bat Full-size Grade 1 English willow cricket bat. Built for professional performance. Sports"},
    {"id": "Tennis Racket", "text": "Tennis Racket Lightweight graphite racket, 270 g. Pre-strung with synthetic gut at 55 lbs. Sports"},
    {"id": "Badminton Racket", "text": "Badminton Racket Aluminum alloy badminton racket, balanced for control. Pre-strung nylon strings. Sports"}
]

def seed():
    print("Initializing Pinecone...")
    pc = Pinecone(api_key=PINECONE_API_KEY)
    
    # Create index if it doesn't exist
    if INDEX_NAME not in pc.list_indexes().names():
        print(f"Creating index {INDEX_NAME}...")
        pc.create_index(
            name=INDEX_NAME,
            dimension=384, # all-MiniLM-L6-v2 dimension
            metric="cosine",
            spec=ServerlessSpec(cloud="aws", region="us-east-1")
        )
    
    index = pc.Index(INDEX_NAME)
    
    print("Loading embedding model...")
    model = SentenceTransformer('all-MiniLM-L6-v2')
    
    print("Generating embeddings and upserting...")
    vectors = []
    for item in PRODUCTS:
        emb = model.encode(item["text"]).tolist()
        vectors.append({
            "id": item["id"],
            "values": emb,
            "metadata": {"name": item["id"]}
        })
    
    # Upsert in a single batch (36 products fits well within limits)
    index.upsert(vectors=vectors)
    print(f"Successfully seeded {len(vectors)} products into Pinecone!")

if __name__ == "__main__":
    seed()
