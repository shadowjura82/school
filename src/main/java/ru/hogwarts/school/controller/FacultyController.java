package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.readFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    public ResponseEntity<Faculty> postFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.createFaculty(faculty));
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.updateFaculty(faculty);
        if (foundFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyService.updateFaculty(faculty));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long id) {
        Faculty faculty = facultyService.deleteFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping(path = "filter")
    public ResponseEntity<Collection<Faculty>> filterByColor(@RequestParam String color) {
        Collection<Faculty> filteredFaculty = facultyService.filterByColor(color);
        return ResponseEntity.ok(filteredFaculty);
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> printAll() {
        return ResponseEntity.ok(facultyService.printAll());
    }

    @GetMapping(path = "filterByColorOrName")
    public ResponseEntity<Collection<Faculty>> findByNameIgnoreCaseOrColorIgnoreCase(@RequestParam String nameOrColor) {
        return ResponseEntity.ok(facultyService.findByNameIgnoreCaseOrColorIgnoreCase(nameOrColor));
    }

    @GetMapping(path = "/{id}/students")
    public ResponseEntity<Collection<Student>> getStudents(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getStudents(id));
    }

    @GetMapping(path = "long-name")
    public ResponseEntity<String> getLongName() {
        return ResponseEntity.ok(facultyService.getLongName());
    }
}