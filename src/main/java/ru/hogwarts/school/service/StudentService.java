package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.FiveLastStudents;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.debug("Constructor has been invoked successfully. StudentService bin has been created in Spring context");
    }

    public Student createStudent(Student student) {
        logger.info("Method createStudent has been invoked successfully");
        return studentRepository.save(student);
    }

    public Student readStudent(Long id) {
        logger.info("Method readStudent has been invoked successfully");
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Student student) {
        logger.info("Method updateStudent has been invoked successfully");
        Student upfatedStudent = readStudent(student.getId());
        if (upfatedStudent == null) {
            logger.error("student with ID " + student.getId() + " was not found");
            return null;
        }
        return studentRepository.save(student);
    }

    public Student deleteStudent(Long id) {
        logger.info("Method deleteStudent has been invoked successfully");
        Student student = readStudent(id);
        if (student == null) {
            logger.error("student with ID " + id + " was not found");
            return null;
        }
        studentRepository.delete(student);
        return student;
    }

    public Collection<Student> filterByAge(int age) {
        logger.info("Method filterByAge has been invoked successfully");
        return studentRepository.findByAge(age);
    }

    public Collection<Student> printAll() {
        logger.info("Method printAll has been invoked successfully");
        return studentRepository.findAll();
    }

    public Collection<Student> findByAgeBetween(Integer startAge, Integer endAge) {
        logger.info("Method findByAgeBetween has been invoked successfully");
        return studentRepository.findByAgeBetween(startAge, endAge);
    }

    public Faculty getFaculty(Long id) {
        logger.info("Method getFaculty has been invoked successfully");
        return studentRepository.findById(id)
                .map(Student::getFaculty)
                .orElse(null);
    }

    public Integer getStudentsAmount() {
        logger.info("Method getStudentsAmount has been invoked successfully");
        return studentRepository.getStudentsAmount();
    }

    public Double getAverageAge() {
        logger.info("Method getAverageAge has been invoked successfully");
        return studentRepository.getAverageAge();
    }

    public List<FiveLastStudents> getFiveLastStudents() {
        logger.info("Method getFiveLastStudents has been invoked successfully");
        return studentRepository.getFiveLastStudents();
    }
}