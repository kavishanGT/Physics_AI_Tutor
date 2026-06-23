import os

from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.vectorstores import FAISS
from langchain.embeddings import HuggingFaceEmbeddings

from ocr_loader import load_scanned_pdf


DATA_PATH = "data/"
VECTOR_PATH = "vector_db/"


# Create vector folder if missing
os.makedirs(VECTOR_PATH, exist_ok=True)


# Multilingual embedding (Sinhala + English)
embedding_model = HuggingFaceEmbeddings(
    model_name="intfloat/multilingual-e5-large"
)


# Text splitter
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=700,
    chunk_overlap=100
)


# Process each book
for file in os.listdir(DATA_PATH):

    if not file.endswith(".pdf"):
        continue

    file_path = os.path.join(DATA_PATH, file)

    book_name = file.replace(".pdf", "")

    save_path = os.path.join(
        VECTOR_PATH,
        book_name
    )

    print("\n==============================")
    print("Processing Book:", book_name)
    print("==============================")

    # Load book with OCR
    documents = load_scanned_pdf(file_path)

    if len(documents) == 0:
        print("Skipping empty book:", file)
        continue

    # Split into chunks
    chunks = text_splitter.split_documents(
        documents
    )

    print("Chunks created:", len(chunks))

    if len(chunks) == 0:
        print("Skipping:", file)
        continue

    # Create FAISS DB
    vector_db = FAISS.from_documents(
        chunks,
        embedding_model
    )

    # Save DB
    vector_db.save_local(save_path)

    print("Saved Vector DB:", save_path)


print("\n🎉 All books processed successfully!")