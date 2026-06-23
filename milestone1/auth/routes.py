from fastapi import APIRouter
from fastapi import Depends
from fastapi import HTTPException

from sqlalchemy.orm import Session

from database.database import get_db

from auth.schemas import *

from auth.service import *


router = APIRouter(
    prefix="/auth",
    tags=["Authentication"]
)


@router.post("/register")
def register(

    req: RegisterRequest,

    db: Session =
    Depends(get_db)

):

    try:

        user = register_user(
            db,
            req
        )

        return {
            "message":
            "User registered successfully",

            "user_id":
            user.user_id
        }

    except Exception as e:

        raise HTTPException(
            status_code=400,
            detail=str(e)
        )