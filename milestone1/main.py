from fastapi import FastAPI

from database.database import engine, Base
from database import models
from auth.routes import router as auth_router

# Create the database tables on startup
Base.metadata.create_all(bind=engine)

app = FastAPI()

app.include_router(
    auth_router
)