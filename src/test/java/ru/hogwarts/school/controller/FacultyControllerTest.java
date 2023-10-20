package ru.hogwarts.school.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

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
    private final Faculty facultyMock = new Faculty("TestingName", "red", new ArrayList<>());

    @AfterEach
    private void clearDatabase() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @Test
    void getFaculty() {
        Faculty faculty = facultyRepository.save(facultyMock);
        ResponseEntity<Faculty> response =
                testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId(),
                        GET, null, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(faculty);
        ResponseEntity<Faculty> responseNull =
                testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + -1,
                        GET, null, Faculty.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void postFaculty() {
        ResponseEntity<Faculty> result = testRestTemplate.exchange("http://localhost:" + port + "/faculty",
                POST, new HttpEntity<>(facultyMock), Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long id = Objects.requireNonNull(result.getBody()).getId();
        facultyMock.setId(id);
        assertThat(facultyRepository.findById(id).orElse(null)).isEqualTo(result.getBody());
        assertThat(facultyRepository.findById(id).orElse(null)).isEqualTo(facultyMock);
    }

    @Test
    void updateFaculty() {
        Faculty faculty = facultyRepository.save(facultyMock);
        faculty.setColor("testing_color");
        ResponseEntity<Faculty> result = testRestTemplate.exchange("http://localhost:" + port + "/faculty",
                PUT, new HttpEntity<>(faculty), Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyRepository.findById(faculty.getId()).orElse(null)).isEqualTo(result.getBody());
        assertThat(facultyRepository.findById(faculty.getId()).orElse(null)).isEqualTo(faculty);

        faculty.setId(-1L);
        result = testRestTemplate.exchange("http://localhost:" + port + "/faculty",
                PUT, new HttpEntity<>(faculty), Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteFaculty() {
        Faculty faculty = facultyRepository.save(facultyMock);
        ResponseEntity<Faculty> result = testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId(),
                DELETE, null, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(faculty);
        result = testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId(),
                DELETE, null, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filterByColor() throws Exception {
        facultyRepository.save(new Faculty("TestingName", "red", null));
        facultyRepository.save(new Faculty("TestingName", "blue", null));
        facultyRepository.save(new Faculty("TestingName", "red", null));
        List<Faculty> addedFaculty = facultyRepository.findAll();
        addedFaculty.remove(1);

        ResponseEntity<List<Faculty>> result = testRestTemplate.exchange("http://localhost:" + port + "/faculty/filter?color=red",
                GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactlyInAnyOrderElementsOf(addedFaculty);
    }

    @Test
    void printAll() throws Exception {
        facultyRepository.save(new Faculty("TestingName", "red", null));
        facultyRepository.save(new Faculty("TestingName", "blue", null));
        facultyRepository.save(new Faculty("TestingName", "red", null));
        List<Faculty> addedFaculty = facultyRepository.findAll();

        ResponseEntity<List<Faculty>> result = testRestTemplate.exchange("http://localhost:" + port + "/faculty",
                GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactlyInAnyOrderElementsOf(addedFaculty);
    }

    @Test
    void findByNameIgnoreCaseOrColorIgnoreCase() throws Exception {
        facultyRepository.save(new Faculty("Mock_name", "red", null));
        facultyRepository.save(new Faculty("TestingName", "blue", null));
        facultyRepository.save(new Faculty("Mock_name", "red", null));
        List<Faculty> filteredFaculty = facultyRepository.findAll();
        filteredFaculty.remove(1);

        ResponseEntity<List<Faculty>> result = testRestTemplate.exchange("http://localhost:" + port + "/faculty/filterByColorOrName?nameOrColor=Red",
                GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactlyInAnyOrderElementsOf(filteredFaculty);
        result = testRestTemplate.exchange("http://localhost:" + port + "/faculty/filterByColorOrName?nameOrColor=mock_name",
                GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactlyInAnyOrderElementsOf(filteredFaculty);
    }

    @Test
    void getStudents() throws Exception {
        Student student1 = studentRepository.save(new Student("name", 23, null));
        Student student2 = studentRepository.save(new Student("name2", 22, null));
        List<Student> listOfStudents = studentRepository.findAll();
        facultyMock.setStudents(listOfStudents);
        Faculty faculty = facultyRepository.save(facultyMock);

        ResponseEntity<List<Student>> response = testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId() + "/students",
                GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyInAnyOrderElementsOf(listOfStudents);
    }
}