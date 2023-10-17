package ru.hogwarts.school;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.*;


public class TestConstants {
    public static final String NAME_CONSTANT = "TestingName";
    public static final int AGE_CONSTANT = 24;
    public static final Long DB_ID_CONSTANT = 1L;
    public static final String COLOR_CONSTANT = "red";
    public static final Faculty FACULTY = new Faculty(DB_ID_CONSTANT, NAME_CONSTANT, COLOR_CONSTANT);
//    public static final Student STUDENT = new Student(DB_ID_CONSTANT, NAME_CONSTANT, AGE_CONSTANT, FACULTY);
    public static final Student STUDENT_WO_FACULTY = new Student(DB_ID_CONSTANT, NAME_CONSTANT, AGE_CONSTANT, null);
    public static final Student STUDENT2 = new Student(2L, "Mock_name", 50, FACULTY);
    public static final List<Student> COLLECTION_OF_STUDENTS = new ArrayList<>(List.of(STUDENT2, STUDENT2));
    public static final Faculty FACULTY2 = new Faculty(DB_ID_CONSTANT, NAME_CONSTANT, COLOR_CONSTANT);
    public static final Student STUDENT3 = new Student(3L, "Mock_name", 24, FACULTY);
    public static final Faculty FACULTY_ID_1 = new Faculty(1L, "TestingName", "red", new ArrayList<>());
    public static final Faculty FACULTY_ID_2 = new Faculty(2L, "Mock_name", "Mock_color", new ArrayList<>());
    public static final Faculty FACULTY_ID_3 = new Faculty(3L, "Mock_name", "red", new ArrayList<>());
    public static final List<Faculty> COLLECTION_OF_FACULTIES = new ArrayList<>(List.of(FACULTY_ID_1, FACULTY_ID_2));
}
