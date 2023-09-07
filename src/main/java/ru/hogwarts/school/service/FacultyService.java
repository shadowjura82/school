package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    Map<Long, Faculty> faculties = new HashMap<>();
    Long id = 0L;

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++id);
        return faculties.put(id, faculty);
    }

    public Faculty readFaculty(Long id) {
        return faculties.get(id);
    }

    public Faculty updateFaculty(Faculty faculty) {
        if (!faculties.containsKey(faculty.getId())) return null;
        return faculties.put(faculty.getId(), faculty);
    }

    public Faculty deleteFaculty(Long id) {
        return faculties.remove(id);
    }

    public Collection<Faculty> filterByColor(String color) {
        return faculties.values().stream()
                .filter(e -> e.getColor().equals(color))
                .collect(Collectors.toList());
    }

    public Collection<Faculty> printAll() {
        return faculties.values();
    }
}
