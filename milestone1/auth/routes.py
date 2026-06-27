import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from fastapi import APIRouter
from fastapi import Depends
from fastapi import HTTPException
from sqlalchemy.orm import Session
from database.database import get_db
from auth.schemas import *
from auth.service import *
from auth.dependencies import get_current_user



router = APIRouter(
    prefix="/auth",
    tags=["Authentication"]
)

@router.get("/me")
def me(

    user=
    Depends(
        get_current_user
    )

):

    return user


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

@router.post("/login")
def login(

    req: LoginRequest,

    db: Session =
    Depends(get_db)

):

    try:

        token = login_user(
            db,
            req
        )

        return {

            "access_token":
            token,

            "token_type":
            "bearer"
        }

    except Exception as e:

        raise HTTPException(
            status_code=401,
            detail=str(e)
        )