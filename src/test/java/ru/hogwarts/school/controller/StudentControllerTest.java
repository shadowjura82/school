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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;

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
    private ObjectMapper mapper = new ObjectMapper();

    @AfterEach
    private void ClearDatabase() {
        studentRepository.deleteAll();
    }

    @Test
    void getStudent() {
        Student student = studentRepository.save(new Student(1L, "TestingName", 24, null));
        ResponseEntity<Student> response =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + student.getId(), Student.class);
        assertThat(response.getBody()).isEqualTo(student);
        ResponseEntity<Student> responseNull =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + -1, Student.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void postStudent() throws Exception {
        Student student = new Student(1L, "TestingName", 24, null);
        ResponseEntity<Student> newStudentResponse =
                testRestTemplate.postForEntity("http://localhost:" + port + "/student", student, Student.class);
        assertThat(newStudentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        student.setId(newStudentResponse.getBody().getId());
        assertThat(student).isEqualTo(newStudentResponse.getBody());
    }

    @Test
    void updateStudentTest() {
        Student student = studentRepository.save(new Student(1L, "TestingName", 24, null));
        HttpEntity<Student> studentHttpEntity = new HttpEntity<>(new Student(2L, "Mock_name", 50, null));
        ResponseEntity<Student> result =
                testRestTemplate.exchange("http://localhost:" + port + "/student", PUT, studentHttpEntity, Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        studentHttpEntity.getBody().setId(student.getId());
        result = testRestTemplate.exchange("http://localhost:" + port + "/student", PUT, studentHttpEntity, Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(studentHttpEntity.getBody());
    }

    @Test
    void deleteStudentTest() {
        Student student = studentRepository.save(new Student(1L, "TestingName", 24, null));
        HttpEntity<Student> studentHttpEntity = new HttpEntity<>(student);
        ResponseEntity<Student> result =
                testRestTemplate.exchange("http://localhost:" + port + "/student/" + student.getId(), DELETE, studentHttpEntity, Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(student);
        result = testRestTemplate.exchange("http://localhost:" + port + "/student/" + student.getId(), DELETE, studentHttpEntity, Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filterByAgeTest() throws Exception {
        studentRepository.save(new Student(1L, "Mock_name", 24, null));
        studentRepository.save(new Student(2L, "Mock_name", 50, null));
        studentRepository.save(new Student(3L, "Mock_name", 24, null));
        List<Student> addedStudents = studentRepository.findAll();
        addedStudents.remove(1);
        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/filter?age=24", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(addedStudents));
    }

    @Test
    void printAllTest() throws Exception {
        studentRepository.save(new Student(1L, "Mock_name", 24, null));
        studentRepository.save(new Student(2L, "Mock_name", 50, null));
        studentRepository.save(new Student(3L, "Mock_name", 24, null));
        List<Student> addedStudents = studentRepository.findAll();
        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(addedStudents));
    }

    @Test
    void findByAgeBetweenTest() throws Exception {
        studentRepository.save(new Student(1L, "Mock_name", 24, null));
        studentRepository.save(new Student(2L, "Mock_name", 50, null));
        studentRepository.save(new Student(3L, "Mock_name", 24, null));
        List<Student> addedStudents = studentRepository.findAll();
        addedStudents.remove(1);
        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/filter_range?startAge=20&endAge=30", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(addedStudents));
    }

    @Test
    void getFaculty() {
        Faculty faculty = facultyRepository.save(new Faculty(1L, "TestingName", "red", new ArrayList<>()));
        Student student = studentRepository.save(new Student(1L, "Mock_name", 24, faculty));
        ResponseEntity<Faculty> response =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + student.getId() + "/faculty", Faculty.class);
        assertThat(response.getBody()).isEqualTo(student.getFaculty());
        ResponseEntity<Faculty> responseNull =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + -1 + "/faculty", Faculty.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getStudentsAmount() {
        Integer result =
                testRestTemplate.getForObject("http://localhost:" + port + "/student/students-count", Integer.class);
        assertThat(result.intValue()).isEqualTo(0);
        Student student = studentRepository.save(new Student(1L, "TestingName", 24, null));
        result =
                testRestTemplate.getForObject("http://localhost:" + port + "/student/students-count", Integer.class);
        assertThat(result.intValue()).isEqualTo(1);
    }

    @Test
    void getAverageAge() {
        Student student1 = studentRepository.save(new Student(1L, "TestingName", 24, null));
        Student student2 = studentRepository.save(new Student(2L, "Mock_name", 50, null));
        Double result =
                testRestTemplate.getForObject("http://localhost:" + port + "/student/students-avg-age", Double.class);
        assertThat(result.doubleValue()).isEqualTo(37);
    }

    @Test
    void getFiveLastStudents() {
        for (int i = 1; i < 11; i++) {
            studentRepository.save(new Student((long) i, "Mock_name", 50, null));
        }
        List<Student> listOfStudents = new ArrayList<>();
        for (int i = 10; i > 5; i--) {
            listOfStudents.add(studentRepository.findById((long) i).orElse(null));
        }
        List result =
                testRestTemplate.getForObject("http://localhost:" + port + "/student/students-five-last", List.class);
        assertThat(result.toString()).isEqualTo(listOfStudents.toString());
    }
}