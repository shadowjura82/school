package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.FiveLastStudents;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        Student student = studentService.readStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<Student> postStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        Student foundStudent = studentService.updateStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentService.updateStudent(student));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        Student student = studentService.deleteStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping(path = "filter")
    public ResponseEntity<Collection<Student>> filterByAge(@RequestParam int age) {
        Collection<Student> filteredStudents = studentService.filterByAge(age);
        return ResponseEntity.ok(filteredStudents);
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> printAll() {
        return ResponseEntity.ok(studentService.printAll());
    }

    @GetMapping(path = "filter_range")
    public ResponseEntity<Collection<Student>> findByAgeBetween(
            @RequestParam Integer startAge,
            @RequestParam Integer endAge) {
        return ResponseEntity.ok(studentService.findByAgeBetween(startAge, endAge));
    }

    @GetMapping(path = "/{id}/faculty")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        Faculty faculty = studentService.getFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping(path = "students-count")
    public ResponseEntity<Integer> getStudentsAmount() {
        return ResponseEntity.ok(studentService.getStudentsAmount());
    }

    @GetMapping(path = "students-avg-age")
    public ResponseEntity<Double> getAverageAge() {
        return ResponseEntity.ok(studentService.getAverageAge());
    }

    @GetMapping(path = "students-five-last")
    public ResponseEntity<List<FiveLastStudents>> getFiveLastStudents() {
        return ResponseEntity.ok(studentService.getFiveLastStudents());
    }

    @GetMapping(path = "sorted-name")
    public ResponseEntity<List<String>> findByNameSorted() {
        return ResponseEntity.ok(studentService.findByNameSorted());
    }

    @GetMapping(path = "average-age")
    public ResponseEntity<Double> findAverageAge() {
        return ResponseEntity.ok(studentService.findAverageAge());
    }

    @GetMapping(path = "intNumber")
    public ResponseEntity<Integer> getIntNumber() {
        return ResponseEntity.ok(studentService.getIntNumber());
    }
}