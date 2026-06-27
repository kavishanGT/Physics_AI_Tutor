from fastapi import APIRouter
from fastapi import Depends

from sqlalchemy.orm import Session

from database.database import get_db

from database.models import User

from auth.permission import require_admin


router = APIRouter(
    prefix="/admin",
    tags=["Admin"]
)

@router.get("/users")
def get_users(

    db: Session = Depends(get_db),

    user=Depends(
        require_admin
    )

):

    users = db.query(User).all()

    return users