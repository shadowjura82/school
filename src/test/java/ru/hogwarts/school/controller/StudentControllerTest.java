package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;
    private final Student studentMock = new Student("TestingName", 24, null);

    @AfterEach
    private void ClearDatabase() {
        studentRepository.deleteAll();
    }

    @Test
    void getStudent() {
        Student student = studentRepository.save(studentMock);
        ResponseEntity<Student> response = testRestTemplate.exchange("http://localhost:" + port + "/student/" + student.getId(),
                GET, null, Student.class);
        assertThat(response.getBody()).isEqualTo(student);
        ResponseEntity<Student> responseNull =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + -1, Student.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void postStudent() throws Exception {
        ResponseEntity<Student> response = testRestTemplate.exchange("http://localhost:" + port + "/student",
                POST, new HttpEntity<>(studentMock), Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Long id = Objects.requireNonNull(response.getBody()).getId();
        studentMock.setId(id);
        assertThat(studentRepository.findById(id).orElse(null)).isEqualTo(studentMock);
        assertThat(studentRepository.findById(id).orElse(null)).isEqualTo(response.getBody());
    }

    @Test
    void updateStudentTest() {
        Student student = studentRepository.save(studentMock);
        student.setAge(50);
        ResponseEntity<Student> result =
                testRestTemplate.exchange("http://localhost:" + port + "/student", PUT, new HttpEntity<>(student), Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentRepository.findById(student.getId()).orElse(null)).isEqualTo(student);
        assertThat(studentRepository.findById(student.getId()).orElse(null)).isEqualTo(result.getBody());
        student.setId(-1L);
        result = testRestTemplate.exchange("http://localhost:" + port + "/student", PUT, new HttpEntity<>(student), Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteStudentTest() {
        Student student = studentRepository.save(studentMock);
        ResponseEntity<Student> result = testRestTemplate.exchange("http://localhost:" + port + "/student/" + student.getId(),
                DELETE, new HttpEntity<>(student), Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(student);
        result = testRestTemplate.exchange("http://localhost:" + port + "/student/" + student.getId(),
                DELETE, new HttpEntity<>(student), Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filterByAgeTest() throws Exception {
        studentRepository.save(new Student("Mock_name", 24, null));
        studentRepository.save(new Student("Mock_name", 50, null));
        studentRepository.save(new Student("Mock_name", 24, null));
        List<Student> addedStudents = studentRepository.findAll();
        addedStudents.remove(1);
        ResponseEntity<List<Student>> result = testRestTemplate.exchange("http://localhost:" + port + "/student/filter?age=24",
                GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactlyInAnyOrderElementsOf(addedStudents);
    }

    @Test
    void printAllTest() throws Exception {
        studentRepository.save(new Student("Mock_name", 24, null));
        studentRepository.save(new Student("Mock_name", 50, null));
        studentRepository.save(new Student("Mock_name", 24, null));
        List<Student> addedStudents = studentRepository.findAll();
        ResponseEntity<List<Student>> response = testRestTemplate.exchange("http://localhost:" + port + "/student",
                GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactlyInAnyOrderElementsOf(addedStudents);
    }

    @Test
    void findByAgeBetweenTest() throws Exception {
        studentRepository.save(new Student("Mock_name", 24, null));
        studentRepository.save(new Student("Mock_name", 50, null));
        studentRepository.save(new Student("Mock_name", 24, null));
        List<Student> addedStudents = studentRepository.findAll();
        addedStudents.remove(1);
        ResponseEntity<List<Student>> result = testRestTemplate.exchange("http://localhost:" + port + "/student/filter_range?startAge=20&endAge=30",
                GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactlyInAnyOrderElementsOf(addedStudents);
    }

    @Test
    void getFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty("TestingName", "red", new ArrayList<>()));
        studentMock.setFaculty(faculty);
        Student student = studentRepository.save(studentMock);
        ResponseEntity<Faculty> response = testRestTemplate.exchange("http://localhost:" + port + "/student/" + student.getId() + "/faculty",
                GET, new HttpEntity<>(student), Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(faculty);
        ResponseEntity<Faculty> responseNull =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + -1 + "/faculty", Faculty.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getStudentsAmount() {
        ResponseEntity<Integer> result = testRestTemplate.exchange("http://localhost:" + port + "/student/students-count",
                GET, null, Integer.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(0);
        assertThat(studentRepository.findAll()).isEmpty();
        Student student = studentRepository.save(studentMock);
        result = testRestTemplate.exchange("http://localhost:" + port + "/student/students-count", GET, null, Integer.class);
        assertThat(result.getBody()).isEqualTo(1);
        assertThat(studentRepository.findAll()).isNotEmpty();
    }

    @Test
    void getAverageAge() {
        Student student1 = studentRepository.save(studentMock);
        Student student2 = studentRepository.save(new Student("Mock_name", 50, null));
        ResponseEntity<Double> result = testRestTemplate.exchange("http://localhost:" + port + "/student/students-avg-age",
                GET, null, Double.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(37);
    }

    @Test
    void getFiveLastStudents() {
        List<Student> listOfStudents = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            Student student = studentRepository.save(new Student("Mock_name", 50, null));
            if (i > 5) {
                listOfStudents.add(student);
            }
        }

        ResponseEntity<List<Student>> result =
                testRestTemplate.exchange("http://localhost:" + port + "/student/students-five-last", GET, null,
                        new ParameterizedTypeReference<>() {
                        });
        assertThat(result.getBody()).containsExactlyInAnyOrderElementsOf(listOfStudents);
    }

    @Test
    public void findByNameSorted() {
        List<Student> listOfStudents = new ArrayList<>(List.of(
                studentRepository.save(new Student("aifth__2", 10, null)),
                studentRepository.save(new Student("aecond__3", 20, null)),
                studentRepository.save(new Student("Azex__5", 30, null)),
                studentRepository.save(new Student("Alex2__4", 40, null)),
                studentRepository.save(new Student("Tenth__8", 50, null))));
        List<String> expectedList = new ArrayList<>(List.of("AECOND__3", "AIFTH__2", "ALEX2__4", "AZEX__5"));

//ResponseEntity<>
    }
}