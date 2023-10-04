package ru.hogwarts.school.service;

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

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student readStudent(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Student student) {
        Student upfatedStudent = readStudent(student.getId());
        if (upfatedStudent == null) {
            return null;
        }
        return studentRepository.save(student);
    }

    public Student deleteStudent(Long id) {
        Student student = readStudent(id);
        if (student == null) {
            return null;
        }
        studentRepository.delete(student);
        return student;
    }

    public Collection<Student> filterByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Collection<Student> printAll() {
        return studentRepository.findAll();
    }

    public Collection<Student> findByAgeBetween(Integer startAge, Integer endAge) {
        return studentRepository.findByAgeBetween(startAge, endAge);
    }

    public Faculty getFaculty(Long id) {
        return studentRepository.findById(id)
                .map(Student::getFaculty)
                .orElse(null);
    }

    public Integer getStudentsAmount() {
        return studentRepository.getStudentsAmount();
    }

    public Double getAverageAge() {
        return studentRepository.getAverageAge();
    }
    public List<FiveLastStudents> getFiveLastStudents() {
        return studentRepository.getFiveLastStudents();
    }
}