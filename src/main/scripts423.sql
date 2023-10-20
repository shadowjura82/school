select
	student.name as "Student Name",
	student.age as "Age of student",
	faculty.name "Faculty Name"
from student
	join faculty on student.faculty_id = faculty.id;


select
	student.name as "Student Name",
	student.age as "Age of student"
from student
	join avatar on student.id = avatar.student_id;