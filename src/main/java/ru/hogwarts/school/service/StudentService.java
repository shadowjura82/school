package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.stream.Collectors;

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
        if (studentRepository.existsById(id)) return studentRepository.findById(id).get();
        return null;
    }

    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student deleteStudent(Long id) {
        Student student = readStudent(id);
        if (student == null) return null;
        studentRepository.deleteById(id);
        return student;
    }

    public Collection<Student> filterByAge(int age) {
        return studentRepository.findAll().stream()
                .filter(e -> e.getAge() == age)
                .collect(Collectors.toList());
    }

    public Collection<Student> printAll() {
        return studentRepository.findAll();
    }

    public Collection<Student> findByAgeBetween(Integer startAge, Integer endAge) {
        return studentRepository.findByAgeBetween(startAge, endAge);
    }

    public Faculty getFaculty(Long Id) {
        if (!studentRepository.existsById(Id)) return null;
        return studentRepository.findById(Id).get().getFaculty();
    }
    public Collection<Student> findByFaculty_id(Long Id) {
        return studentRepository.findByFaculty_id(Id);
    }
}