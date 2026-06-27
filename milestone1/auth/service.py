import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from sqlalchemy.orm import Session
from database.models import User
from auth.security import *
from auth.schemas import *



def register_user(
    db: Session,
    req: RegisterRequest
):

    existing_user = (
        db.query(User)
        .filter(
            User.email == req.email
        )
        .first()
    )

    if existing_user:

        raise Exception(
            "Email already exists"
        )

    user = User(

        full_name=req.full_name,

        email=req.email,

        password_hash=
        hash_password(
            req.password
        ),

        role=req.role

    )

    db.add(user)

    db.commit()

    db.refresh(user)

    return user


def login_user(
    db: Session,
    req: LoginRequest
):

    user = (
        db.query(User)
        .filter(
            User.email == req.email
        )
        .first()
    )

    if not user:

        raise Exception(
            "Invalid email"
        )

    if not verify_password(
        req.password,
        user.password_hash
    ):

        raise Exception(
            "Invalid password"
        )

    token = create_access_token(

        {
            "user_id":
            user.user_id,

            "email":
            user.email,

            "role":
            user.role
        }

    )

    return token