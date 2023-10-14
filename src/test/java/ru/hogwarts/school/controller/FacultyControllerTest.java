package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static ru.hogwarts.school.TestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentRepository studentRepository;

    @AfterEach
    private void clearDatabase() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void getFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty(1L, "TestingName", "red", null));
        ResponseEntity<Faculty> response =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + faculty.getId(), Faculty.class);
        assertThat(response.getBody()).isEqualTo(faculty);
        ResponseEntity<Faculty> responseNull =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + -1, Faculty.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void postFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty(1L, "TestingName", "red", null));
        ResponseEntity<Faculty>
                newFacultyResponse = testRestTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, Faculty.class);
        assertThat(newFacultyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyRepository.findById(faculty.getId()).orElse(null)).isEqualTo(faculty);
    }

    @Test
    void updateFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty(1L, "TestingName", "red", null));
        HttpEntity<Faculty> facultyHttpEntity = new HttpEntity<>(new Faculty(2L, "TestingName", "red", null));
        ResponseEntity<Faculty> result =
                testRestTemplate.exchange("http://localhost:" + port + "/faculty", PUT, facultyHttpEntity, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        facultyHttpEntity.getBody().setId(faculty.getId());
        result = testRestTemplate.exchange("http://localhost:" + port + "/faculty", PUT, facultyHttpEntity, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(facultyHttpEntity.getBody());
    }

    @Test
    void deleteFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty(1L, "TestingName", "red", null));
        ResponseEntity<Faculty> result =
                testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId(), DELETE, null, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(faculty);
        result = testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId(), DELETE, null, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filterByColor() throws Exception {
        facultyRepository.save(new Faculty(1L, "TestingName", "red", null));
        facultyRepository.save(new Faculty(2L, "TestingName", "blue", null));
        facultyRepository.save(new Faculty(3L, "TestingName", "red", null));
        List<Faculty> addedFaculty = facultyRepository.findAll();
        addedFaculty.remove(1);
        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/filter?color=red", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().toString()).isEqualTo(addedFaculty.toString());
    }

    @Test
    void printAll() throws Exception {
        facultyRepository.save(new Faculty(1L, "TestingName", "red", null));
        facultyRepository.save(new Faculty(2L, "TestingName", "blue", null));
        facultyRepository.save(new Faculty(3L, "TestingName", "red", null));
        List<Faculty> addedFaculty = facultyRepository.findAll();
        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().toString()).isEqualTo(addedFaculty.toString());
    }

    @Test
    void findByNameIgnoreCaseOrColorIgnoreCase() throws Exception {
        facultyRepository.save(new Faculty(1L, "Mock_name", "red", null));
        facultyRepository.save(new Faculty(2L, "TestingName", "blue", null));
        facultyRepository.save(new Faculty(3L, "Mock_name", "red", null));
        List<Faculty> filteredFaculty = facultyRepository.findAll();
        filteredFaculty.remove(1);
        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/filterByColorOrName?nameOrColor=Red", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().toString()).isEqualTo(filteredFaculty.toString());
        result = testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/filterByColorOrName?nameOrColor=mock_name", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().toString()).isEqualTo(filteredFaculty.toString());
    }

    @Test
    void getStudents() throws JsonProcessingException {
        Faculty faculty = facultyRepository.save(new Faculty(1L, "name", "blue", null));
        Student student1 = studentRepository.save(new Student(1L, "name", 23, null));
        Student student2 = studentRepository.save(new Student(2L, "name2", 22, null));
        List<Student> listOfStudents = studentRepository.findAll();
        faculty.setStudents(listOfStudents);
        facultyRepository.save(faculty);
        ResponseEntity<List> response =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + faculty.getId() + "/students", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).isEqualTo(faculty.getStudents().toString());
        studentRepository.deleteAll();
    }
}