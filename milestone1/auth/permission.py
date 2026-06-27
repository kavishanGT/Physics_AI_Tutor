import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from fastapi import Depends
from fastapi import HTTPException
from auth.dependencies import get_current_user

def require_student(
    user = Depends(get_current_user)
):
    if user["role"] != "student":
        raise HTTPException(
            status_code=403,
            detail="Student access required"
        )
    return user

def require_teacher(
    user = Depends(get_current_user)
):
    if user["role"] not in ["teacher", "admin"]:
        raise HTTPException(
            status_code=403,
            detail="Teacher access required"
        )
    return user


def require_admin(

    user=
    Depends(
        get_current_user
    )

):

    if user["role"] != "admin":

        raise HTTPException(

            status_code=403,

            detail=
            "Admin access required"

        )

    return user