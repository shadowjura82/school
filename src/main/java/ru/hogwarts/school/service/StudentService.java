package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {
    Map<Long, Student> students = new HashMap<>();
    Long id = 0L;

    public Student createStudent(Student student) {
        student.setId(++id);
        return students.put(id, student);
    }

    public Student readStudent(Long id) {
        return students.get(id);
    }

    public Student updateStudent(Student student) {
        return students.put(student.getId(), student);
    }

    public Student deleteStudent(Long id) {
        return students.remove(id);
    }

    public Collection<Student> filterByAge(int age) {
        return students.values().stream()
                .filter(e -> e.getAge() == age)
                .collect(Collectors.toList());
    }

    public Collection<Student> printAll() {
        return students.values();
    }
}
