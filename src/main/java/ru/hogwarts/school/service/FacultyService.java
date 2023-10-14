package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
        logger.debug("Constructor has been invoked successfully. FacultyService bin has been created in Spring context");
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Method createFaculty has been invoked successfully");
        return facultyRepository.save(faculty);
    }

    public Faculty readFaculty(Long id) {
        logger.info("Method readFaculty has been invoked successfully");
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty updateFaculty(Faculty faculty) {
        logger.info("Method updateFaculty has been invoked successfully");
        Faculty upfatedFaculty = readFaculty(faculty.getId());
        if (upfatedFaculty == null) {
            logger.error("student with ID " + faculty.getId() + " was not found");
            return null;
        }
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(Long id) {
        logger.info("Method deleteFaculty has been invoked successfully");
        Faculty faculty = readFaculty(id);
        if (faculty == null) {
            logger.error("student with ID " + id + " was not found");
            return null;
        }
        facultyRepository.delete(faculty);
        return faculty;
    }

    public Collection<Faculty> filterByColor(String color) {
        logger.info("Method filterByColor has been invoked successfully");
        return facultyRepository.findByColorLikeIgnoreCase(color);
    }

    public Collection<Faculty> printAll() {
        logger.info("Method printAll has been invoked successfully");
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String nameOrColor) {
        logger.info("Method findByNameIgnoreCaseOrColorIgnoreCase has been invoked successfully");
        return facultyRepository.findByNameContainsIgnoreCaseOrColorContainsIgnoreCase(nameOrColor, nameOrColor);
    }

    public Collection<Student> getStudents(Long id) {
        logger.info("Method getStudents has been invoked successfully");
        return facultyRepository.findById(id)
                .map(Faculty::getStudents)
                .orElseGet(Collections::emptyList);
    }
}