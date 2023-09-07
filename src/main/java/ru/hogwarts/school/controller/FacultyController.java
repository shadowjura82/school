package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    public ResponseEntity<Faculty> getFacility(@PathVariable Long id) {
        Faculty faculty = facultyService.readFaculty(id);
        if (faculty == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    public ResponseEntity<Faculty> postFacility(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.createFaculty(faculty));
    }

    @PutMapping
    public ResponseEntity<Faculty> updateFacility(@RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.updateFaculty(faculty);
        if (foundFaculty == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(facultyService.updateFaculty(faculty));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFacility(@PathVariable Long id) {
        Faculty faculty = facultyService.deleteFaculty(id);
        if (faculty == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(faculty);
    }

    @GetMapping(path = "filter")
    public ResponseEntity<Collection<Faculty>> filterByColor(@RequestParam String color) {
        Collection<Faculty> filteredFaculty = facultyService.filterByColor(color);
        if (filteredFaculty.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(filteredFaculty);
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> printAll() {
        if (facultyService.printAll().isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(facultyService.printAll());
    }
}
