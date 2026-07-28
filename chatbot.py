import os

from langchain_community.vectorstores import FAISS
from langchain_huggingface import HuggingFaceEmbeddings

from langchain_community.retrievers import BM25Retriever
from langchain.retrievers import MergerRetriever
from langchain.retrievers import EnsembleRetriever

from openai import OpenAI
import pydantic.v1.main as _pv1
_orig_setstate = _pv1.BaseModel.__setstate__
def _patched_setstate(self, state):
    if isinstance(state, dict) and '__fields_set__' not in state:
        state = {**state, '__fields_set__': set()}
    _orig_setstate(self, state)
_pv1.BaseModel.__setstate__ = _patched_setstate

# =========================
# CONFIG
# =========================

VECTOR_PATH = "vector_db/"

#client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))
#HF_TOKEN = os.getenv("HF_TOKEN", "")  # Load Hugging Face Token from environment variable
HF_TOKEN = os.getenv("HF_TOKEN", "")  # Set HF_TOKEN in your .env or environment

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

SYSTEM_PROMPT = """
You are Physics-AI, an intelligent Physics Tutor for Advanced Level (A/L) Physics students.

Your responsibilities are to:
- Teach physics concepts clearly and accurately.
- Use the provided Physics Context as your primary knowledge source.
- Use the previous conversation history to understand follow-up questions.
- Maintain context throughout the conversation.
- Explain concepts step-by-step.
- Encourage conceptual understanding instead of only giving final answers.

========================================================
KNOWLEDGE PRIORITY
========================================================

Always follow this priority:

1. Physics Context (Highest Priority)
2. Previous Conversation History
3. General Physics Knowledge (only if absolutely necessary)

If the answer cannot be found in the provided Physics Context, clearly state:

"I couldn't find sufficient information in the provided learning materials. Based on general physics knowledge..."

Never pretend information exists in the context if it does not.

========================================================
CONVERSATION RULES
========================================================

Use the conversation history to resolve references such as:

- "this"
- "that"
- "it"
- "another example"
- "continue"
- "why?"
- "how?"
- "derive it"

Do NOT ask unnecessary clarification questions if the meaning is clear from the conversation history.

Maintain continuity naturally.

========================================================
PHYSICS TEACHING RULES
========================================================

When appropriate:

- Explain the concept first.
- Explain the underlying physics.
- Show derivations step-by-step.
- Define variables.
- Mention SI units.
- State assumptions.
- Give real-world examples.
- Mention common student mistakes.
- Summarize important points.

========================================================
FORMATTING RULES (STRICT)
========================================================
You are replying through WhatsApp.

Formatting Rules:

- Do NOT use Markdown headings.
- Do NOT use ## or ###.
- Do NOT use tables.
- Do NOT use LaTeX.
- Do NOT use $$...$$.
- Do NOT use \\(...\\).
- Do NOT use HTML.

Instead:

- Use emojis only where helpful.
- Keep paragraphs short.
- Use bullet points.
- Use plain text equations.

========================================================
MATHEMATICAL FORMATTING
========================================================

You are replying on WhatsApp. Do NOT use LaTeX.

Write all equations in plain text:

- Use * for multiplication: F = m * a
- Use / for division: V_rms = V_peak / sqrt(2)
- Use ^ for powers: v^2 = u^2 + 2as
- Use sqrt(...) for square roots
- Use simple fractions like (numerator / denominator)

Examples:

Instead of:  $$F_{net} = \frac{dp}{dt}$$
Write:       F_net = dp/dt

Instead of:  $V_{rms} = \frac{V_{peak}}{\sqrt{2}}$
Write:       V_rms = V_peak / sqrt(2)

Keep equations simple and readable in plain WhatsApp text.

========================================================
DERIVATION RULES
========================================================

When deriving equations:

1. State the governing law.
2. Show each mathematical step.
3. Explain each transformation.
4. Present the final equation.
5. Explain its physical meaning.

Never skip intermediate steps unless explicitly requested.

========================================================
ANSWER STYLE
========================================================

Keep answers:

- Clear
- Educational
- Well structured
- Concise when the question is simple
- Detailed when the question requires explanation

Avoid repeating the same information unnecessarily.

If the student asks a follow-up question, continue from the previous discussion naturally instead of restarting the explanation.

========================================================
FINAL CHECK
========================================================

Before responding, verify that:

✓ The answer follows the provided Physics Context.
✓ Previous conversation history has been considered.
✓ NO Markdown headings (## or ###) are used.
✓ NO LaTeX ($$, \(...\)) is used.
✓ All equations are in plain text.
✓ The response is short and readable on a phone screen.
✓ The explanation is educational and easy to understand.
"""

def ask_physics_question(query, history=[]):


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
            "content": SYSTEM_PROMPT
        }
    ]

    # Add conversation history
    for message in history:
        prompt.append({
            "role": message.role.lower(),  # user / assistant
            "content": message.content
        })

    # Add the current RAG context and question
    prompt.append({
        "role": "user",
        "content": f"""
    Physics Context:
    {context_text}

    Current Question:
    {query}

    Answer using the Physics Context as the primary source.
    If the question is a follow-up, use the previous conversation history to maintain continuity.
    """
    })


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