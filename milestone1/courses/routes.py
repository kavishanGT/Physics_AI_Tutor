from fastapi import APIRouter
from fastapi import Depends
from fastapi import HTTPException

from sqlalchemy.orm import Session

from database.database import get_db

from courses.schemas import *

from courses.service import *

from auth.dependencies import get_current_user

from auth.permission import (
    require_teacher,
    require_admin
)

router = APIRouter(

    prefix="/courses",

    tags=["Courses"]

)

@router.post("/create")
def create_course_api(

    req: CourseCreate,

    db: Session =
    Depends(get_db),

    user=
    Depends(
        require_teacher
    )

):

    course = create_course(

        db,

        req,

        user["user_id"]

    )

    return course

@router.get("/")
def get_courses(

    db: Session =
    Depends(get_db)

):

    return get_all_courses(
        db
    )

@router.get("/{course_id}")
def get_course_api(

    course_id: int,

    db: Session =
    Depends(get_db)

):

    course = get_course(
        db,
        course_id
    )

    if not course:

        raise HTTPException(

            status_code=404,

            detail=
            "Course not found"

        )

    return course


@router.put("/{course_id}")
def update_course_api(

    course_id: int,

    req: CourseUpdate,

    db: Session =
    Depends(get_db),

    user=
    Depends(
        get_current_user
    )

):

    course = get_course(
        db,
        course_id
    )

    if not course:

        raise HTTPException(
            status_code=404,
            detail="Course not found"
        )

    if (

        course.created_by
        != user["user_id"]

        and

        user["role"]
        != "admin"

    ):

        raise HTTPException(

            status_code=403,

            detail=
            "Not allowed"

        )

    return update_course(
        db,
        course,
        req
    )

@router.delete("/{course_id}")
def delete_course_api(

    course_id: int,

    db: Session =
    Depends(get_db),

    user=
    Depends(
        require_admin
    )

):

    course = get_course(
        db,
        course_id
    )

    if not course:

        raise HTTPException(
            status_code=404,
            detail="Course not found"
        )

    delete_course(
        db,
        course
    )

    return {

        "message":
        "Course deleted"
    }