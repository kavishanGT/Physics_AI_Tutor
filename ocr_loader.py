import os
import pytesseract
from pdf2image import convert_from_path
from langchain.schema import Document

# Set paths
pytesseract.pytesseract.tesseract_cmd = \
r"C:\Program Files\Tesseract-OCR\tesseract.exe"

POPPLER_PATH = r"C:\poppler-25.12.0\Library\bin"


def load_scanned_pdf(file_path):

    print(f"\nProcessing OCR: {file_path}")

    pages = convert_from_path(
        file_path,
        poppler_path=POPPLER_PATH
    )

    documents = []

    for i, page in enumerate(pages):

        text = pytesseract.image_to_string(
            page,
            lang="eng"
        )

        if text.strip() == "":
            continue

        doc = Document(
            page_content=text,
            metadata={
                "page": i + 1,
                "source": file_path
            }
        )

        documents.append(doc)

    print("Pages extracted:", len(documents))

    return documents