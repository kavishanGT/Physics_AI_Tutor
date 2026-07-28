from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from database.database import engine, Base
from database import models
from auth.routes import router as auth_router
from courses.routes import router as courses_router
from admin.routes import router as admin_router
from classes.routes import (
    router as classes_router
)


# Create the database tables on startup
Base.metadata.create_all(bind=engine)

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth_router)
app.include_router(courses_router)
app.include_router(admin_router)
app.include_router(classes_router)
