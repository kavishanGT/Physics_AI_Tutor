import urllib.parse
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.orm import declarative_base


# URL-encode the password and database name to handle special characters (like '#' and spaces)
password = urllib.parse.quote_plus("Tashin20010810##")
db_name = urllib.parse.quote_plus("physicsLMS")

DATABASE_URL = f"postgresql://postgres:{password}@localhost:5432/{db_name}"

engine = create_engine(DATABASE_URL)

SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine
)

Base = declarative_base()


def get_db():

    db = SessionLocal()

    try:
        yield db

    finally:
        db.close()