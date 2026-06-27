import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from sqlalchemy import *
from database.database import Base


class User(Base):

    __tablename__ = "users"

    user_id = Column(Integer, primary_key=True)

    full_name = Column(String)

    email = Column(String)

    password_hash = Column(Text)

    role = Column(String)


class Course(Base):

    __tablename__ = "courses"

    course_id = Column(Integer, primary_key=True)

    title = Column(String)

    description = Column(Text)

    created_by = Column(ForeignKey("users.user_id"))

    created_at = Column(DateTime(timezone=True))

class Classtype(Base):

    __tablename__ = "classes"

    class_id = Column(Integer, primary_key=True)

    course_id = Column(ForeignKey("courses.course_id"))

    teacher_id = Column(ForeignKey("users.user_id"))

    class_name = Column(String)

    created_at = Column(DateTime(timezone=True))

class Enrollment(Base):

    __tablename__ = "enrollments"

    enrollment_id = Column(Integer, primary_key=True)

    class_id = Column(ForeignKey("classes.class_id"))

    student_id = Column(ForeignKey("users.user_id"))

    enrolled_at = Column(DateTime(timezone=True))




    