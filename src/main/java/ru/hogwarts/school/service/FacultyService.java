package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty readFaculty(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty updateFaculty(Faculty faculty) {
        Faculty upfatedFaculty = readFaculty(faculty.getId());
        if (upfatedFaculty == null) {
            return null;
        }
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(Long id) {
        Faculty faculty = readFaculty(id);
        if (faculty == null) {
            return null;
        }
        facultyRepository.delete(faculty);
        return faculty;
    }

    public Collection<Faculty> filterByColor(String color) {
        return facultyRepository.findByColorLikeIgnoreCase(color);
    }

    public Collection<Faculty> printAll() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String nameOrColor) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
    }

    public Collection<Student> getStudents(Long id) {
        return facultyRepository.findById(id)
                .map(Faculty::getStudents)
                .orElseGet(Collections::emptyList);
    }
}