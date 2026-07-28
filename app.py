from fastapi import FastAPI
from pydantic import BaseModel

from chatbot import ask_physics_question

from fastapi.middleware.cors import CORSMiddleware


# =========================
# INIT APP
# =========================

app = FastAPI(
    title="Physics AI Tutor API",
    version="1.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
# =========================
# REQUEST MODEL
# =========================

class ConversationMessage(BaseModel):
    role: str
    content: str


class QuestionRequest(BaseModel):
    question: str
    history: list[ConversationMessage] = []


# =========================
# HEALTH CHECK
# =========================

@app.get("/")

def root():

    return {

        "message":
        "Physics AI Tutor API Running"
    }


# =========================
# MAIN CHAT ENDPOINT
# =========================

@app.post("/ask")

def ask_question(req: QuestionRequest):

    answer = ask_physics_question(
        req.question,
        req.history
    )

    return {

        "question": req.question,
        "answer": answer

    }