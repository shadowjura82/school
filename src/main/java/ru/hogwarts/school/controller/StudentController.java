package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

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
        if (student == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public ResponseEntity<Student> postStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @PutMapping
    public ResponseEntity<Student> updateStudent(@RequestBody Student student) {
        Student foundStudent = studentService.updateStudent(student);
        if (foundStudent == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(studentService.updateStudent(student));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        Student student = studentService.deleteStudent(id);
        if (student == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(student);
    }

    @GetMapping(path = "filter")
    public ResponseEntity<Collection<Student>> filterByAge(@RequestParam int age) {
        Collection<Student> filteredStudents = studentService.filterByAge(age);
        if (filteredStudents.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(filteredStudents);
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> printAll() {
        if (studentService.printAll().isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(studentService.printAll());
    }

    @GetMapping(path = "filter_range")
    public ResponseEntity<Collection<Student>> findByAgeBetween(
            @RequestParam Integer startAge,
            @RequestParam Integer endAge) {
        if (studentService.printAll().isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(studentService.findByAgeBetween(startAge, endAge));
    }

    @GetMapping(path = "getFaculty")
    public ResponseEntity<Faculty> getFaculty(@RequestParam Long Id) {
        if (studentService.printAll().isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(studentService.getFaculty(Id));
    }
}