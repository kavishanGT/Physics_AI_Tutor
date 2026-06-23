import os

from langchain.vectorstores import FAISS
from langchain.embeddings import HuggingFaceEmbeddings
from langchain.retrievers import EnsembleRetriever
from langchain.retrievers import BM25Retriever
from langchain.retrievers import MergerRetriever

from openai import OpenAI


# =========================
# CONFIG
# =========================

VECTOR_PATH = "vector_db/"

#client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))
HF_TOKEN = os.getenv("HF_TOKEN", "")  # Load Hugging Face Token from environment variable


# # MODEL = "google/flan-t5-large"
# import openai; print(hasattr(openai, 'OpenAI'))

# print(OpenAI)

client = OpenAI(
    base_url="https://router.huggingface.co/v1",
    api_key= HF_TOKEN,
)

MODEL_NAME = "openai/gpt-oss-120b:groq"


# =========================
# LOAD EMBEDDING MODEL
# =========================

embedding_model = HuggingFaceEmbeddings(
    model_name="intfloat/multilingual-e5-large"
)


# =========================
# LOAD ALL VECTOR DATABASES
# =========================
all_docs = []
vector_dbs = []

print("\nLoading Vector Databases...\n")

for folder in os.listdir(VECTOR_PATH):

    db_path = os.path.join(
        VECTOR_PATH,
        folder
    )

    if os.path.isdir(db_path):

        print("Loaded:", folder)

        db = FAISS.load_local(
            db_path,
            embedding_model,
            allow_dangerous_deserialization=True
        )

        vector_dbs.append(db)

        doc = db.similarity_search('physics', k=1000)
        all_docs.extend(doc)




# =========================
# CREATE RETRIEVERS
# =========================

retrievers = []

for db in vector_dbs:

    retriever = db.as_retriever(
        search_type="similarity",
        search_kwargs={"k": 3}
    )

    retrievers.append(retriever)


# =========================
# MERGE RETRIEVERS
# =========================

combined_retriever = MergerRetriever(
    retrievers=retrievers
)

bm25_retriever = BM25Retriever.from_documents(
    all_docs,
)

bm25_retriever.k = 4

combined_retriever = EnsembleRetriever(
    retrievers=retrievers,
    bm25_retriever=bm25_retriever,
    weights=[0.7, 0.3]
)

print("\nAll retrievers merged!\n")


import re

def fix_math_format(text):

    # Convert [ equation ] → $$ equation $$
    text = re.sub(
        r'\[\s*(.*?)\s*\]',
        r'$$\1$$',
        text
    )

    # Remove \displaystyle
    text = re.sub(
        r'\\displaystyle',
        '',
        text
    )

    return text


def fix_table_format(text):

    text = text.replace(
        "| |",
        "|\n|"
    )

    return text
# =========================
# QUERY FUNCTION
# =========================

def ask_physics_question(query):

    print("\nSearching knowledge base...\n")

    # Retrieve documents
    docs = combined_retriever.get_relevant_documents(
        query
    )

    context_text = ""

    for doc in docs:

        context_text += doc.page_content + "\n\n"


    # Build prompt
    prompt = [
        {
            "role": "system",
            "content": """
You are an expert Physics tutor.

Answer the student's question
using the given physics context.

You MUST strictly follow formatting rules.

============================
FORMATTING RULES (STRICT)
============================

1. Use Markdown formatting.
2. Use headings like:

   ## Section Title

3. Use bullet points where needed.
4. Tables MUST be multi-line Markdown tables.
5. NEVER write tables in one line.

============================
MATH FORMATTING RULES
============================

All mathematical expressions MUST use LaTeX.

Inline math format:

$ a = b $

Block math format:

$$
F = ma
$$

NEVER use:

[ equation ]

NEVER use:

(\displaystyle equation)

NEVER use:

boxed equations without $$.

Correct example:

## Newton's Second Law

The net force is:

$$
F = ma
$$

Example derivation:

$$
f_k = \mu mg
$$

Apply Newton's law:

$$
\sum F = ma
$$

Therefore:

$$
a = -\mu g
$$

Always follow this formatting.
Never output equations inside square brackets.
"""
        },

        {
            "role": "user",
            "content": f"""
Context:
{context_text}

Question:
{query}
"""
        }
    ]


    # Call Groq LLM
    completion = client.chat.completions.create(
        model=MODEL_NAME,
        messages=prompt,
    )


    answer = completion.choices[0].message.content
    answer = fix_math_format(answer)
    answer = fix_table_format(answer)
    return answer


# # =========================
# # CHAT LOOP
# # =========================

# print("\nPhysics AI Tutor Ready!\n")

# while True:

#     query = input("Ask Physics Question: ")

#     if query.lower() in ["exit", "quit"]:
#         break

#     answer = ask_physics_question(query)

#     print("\nAnswer:\n")
#     print(answer)
#     print("\n---------------------------\n")