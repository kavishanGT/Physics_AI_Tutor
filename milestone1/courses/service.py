import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from database.models import Course

def create_course(
    db,
    req,
    user_id
):
    course = Course(
        title=req.title,
        description=req.description,
        created_by=user_id
    )
    db.add(course)
    db.commit()
    db.refresh(course)
    return course


def get_all_courses(db):

    return db.query(
        Course
    ).all()

def get_course(
    db,
    course_id
):

    return (

        db.query(Course)

        .filter(
            Course.course_id == course_id
        )

        .first()

    )


def update_course(

    db,

    course,

    req

):

    course.title = req.title

    course.description = req.description

    db.commit()

    db.refresh(course)

    return course

def delete_course(
    db,
    course
):

    db.delete(course)

    db.commit()