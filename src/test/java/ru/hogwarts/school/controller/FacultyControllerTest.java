package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static ru.hogwarts.school.TestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    private Faculty newFaculty(Faculty faculty) {
        ResponseEntity<Faculty> newFacultyResponse =
                testRestTemplate.postForEntity("http://localhost:" + port + "/faculty", faculty, Faculty.class);
        assertThat(newFacultyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return newFacultyResponse.getBody();
    }

    private void deleteFaculty(Long id) {
        testRestTemplate.delete("http://localhost:" + port + "/faculty/" + id);
    }

    @Test
    void getFaculty() {
        Faculty faculty = newFaculty(FACULTY);
        ResponseEntity<Faculty> response =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + faculty.getId(), Faculty.class);
        Faculty studentResponse = response.getBody();
        assertThat(studentResponse).isEqualTo(faculty);
        ResponseEntity<Faculty> responseNull =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + -1, Faculty.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        deleteFaculty(faculty.getId());
    }

    @Test
    void postFaculty() {
        Faculty faculty = newFaculty(FACULTY);
        deleteFaculty(faculty.getId());
    }

    @Test
    void updateFaculty() {
        Faculty faculty = newFaculty(FACULTY);

        HttpEntity<Faculty> facultyHttpEntity = new HttpEntity<>(FACULTY_ID_2);
        ResponseEntity<Faculty> result =
                testRestTemplate.exchange("http://localhost:" + port + "/faculty", PUT, facultyHttpEntity, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        facultyHttpEntity.getBody().setId(faculty.getId());
        result = testRestTemplate.exchange("http://localhost:" + port + "/faculty", PUT, facultyHttpEntity, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(facultyHttpEntity.getBody());
        deleteFaculty(faculty.getId());
    }

    @Test
    void deleteFaculty() {
        Faculty faculty = newFaculty(FACULTY);
        HttpEntity<Faculty> facultyHttpEntity = new HttpEntity<>(faculty);

        ResponseEntity<Faculty> result =
                testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId(), DELETE, facultyHttpEntity, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(facultyHttpEntity.getBody());
        result = testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + faculty.getId(), DELETE, facultyHttpEntity, Faculty.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filterByColor() throws Exception {
        List<Faculty> addedFaculty = new ArrayList<>(List.of(newFaculty(FACULTY_ID_1), newFaculty(FACULTY_ID_2), newFaculty(FACULTY_ID_3)));
        List<Faculty> filteredFaculty = addedFaculty.stream()
                .filter(s -> s.getColor().equals("red"))
                .collect(Collectors.toList());

        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/filter?color=red", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(filteredFaculty));
        addedFaculty.forEach(s -> deleteFaculty(s.getId()));
    }

    @Test
    void printAll() throws Exception {
        List<Faculty> addedFaculty = new ArrayList<>(List.of(newFaculty(FACULTY_ID_1), newFaculty(FACULTY_ID_2), newFaculty(FACULTY_ID_3)));

        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(addedFaculty));
        addedFaculty.forEach(s -> deleteFaculty(s.getId()));
    }

    @Test
    void findByNameIgnoreCaseOrColorIgnoreCase() throws Exception {
        List<Faculty> addedFaculty = new ArrayList<>(List.of(newFaculty(FACULTY_ID_1), newFaculty(FACULTY_ID_2), newFaculty(FACULTY_ID_3)));
        List<Faculty> filteredFaculty = addedFaculty.stream()
                .filter(s -> s.getColor().equals("red"))
                .collect(Collectors.toList());

        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/filterByColorOrName?nameOrColor=Red", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(filteredFaculty));

        filteredFaculty = addedFaculty.stream()
                .filter(s -> s.getName().equals("Mock_name"))
                .collect(Collectors.toList());

        result = testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/filterByColorOrName?nameOrColor=mock_name", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(filteredFaculty));
        addedFaculty.forEach(s -> deleteFaculty(s.getId()));
    }

    @Test
    void getStudents() {
        Student student1 = new Student(1L, "name", 23, null);
        Student student2 = new Student(2L, "name2", 22, null);
        Faculty facultyToSave = new Faculty(1L, "name", "blue", null);
        student1.setFaculty(facultyToSave);
        student2.setFaculty(facultyToSave);
        List<Student> listOfStudents = new ArrayList<>(List.of(student1, student2));
        facultyToSave.setStudents(listOfStudents);
        testRestTemplate.postForEntity("http://localhost:" + port + "/student", student1, Student.class);
        testRestTemplate.postForEntity("http://localhost:" + port + "/student", student2, Student.class);
        Faculty faculty = newFaculty(facultyToSave);

        ResponseEntity<List> response =
                testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + faculty.getId() + "/students", List.class);
        assertThat(response.getBody()).isEqualTo(faculty.getStudents());
        response = testRestTemplate.getForEntity("http://localhost:" + port + "/faculty/" + -1 + "/students", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        deleteFaculty(faculty.getId());
    }
}
