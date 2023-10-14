-- liquibase formatted sql

-- changeset YuriiYatsenko:1
CREATE INDEX student_name ON student(name);

-- changeset YuriiYatsenko:2
create index faculty_name_color on faculty(name,color);