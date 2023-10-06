package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static ru.hogwarts.school.TestConstants.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private StudentRepository studentRepository;
    private ObjectMapper mapper = new ObjectMapper();

    private Student newStudent(Student student) {
        ResponseEntity<Student> newStudentResponse =
                testRestTemplate.postForEntity("http://localhost:" + port + "/student", student, Student.class);
        assertThat(newStudentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        return newStudentResponse.getBody();
    }

    private void deleteStudent(Long id) {
        testRestTemplate.delete("http://localhost:" + port + "/student/" + id);
    }

    @Test
    void getStudent() {
        Student student = newStudent(STUDENT);
        ResponseEntity<Student> response =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + student.getId(), Student.class);
        Student studentResponse = response.getBody();
        assertThat(studentResponse).isEqualTo(student);
        ResponseEntity<Student> responseNull =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + -1, Student.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        deleteStudent(student.getId());
    }

    @Test
    void postStudent() {
        Student student = newStudent(STUDENT);
        deleteStudent(student.getId());
    }

    @Test
    void updateStudentTest() {
        Student student = newStudent(STUDENT);

        HttpEntity<Student> studentHttpEntity = new HttpEntity<>(STUDENT2);
        ResponseEntity<Student> result =
                testRestTemplate.exchange("http://localhost:" + port + "/student", PUT, studentHttpEntity, Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        studentHttpEntity.getBody().setId(student.getId());
        result = testRestTemplate.exchange("http://localhost:" + port + "/student", PUT, studentHttpEntity, Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(studentHttpEntity.getBody());
        deleteStudent(student.getId());
    }

    @Test
    void deleteStudentTest() {
        Student student = newStudent(STUDENT);
        HttpEntity<Student> studentHttpEntity = new HttpEntity<>(student);

        ResponseEntity<Student> result =
                testRestTemplate.exchange("http://localhost:" + port + "/student/" + student.getId(), DELETE, studentHttpEntity, Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(studentHttpEntity.getBody());
        result = testRestTemplate.exchange("http://localhost:" + port + "/student/" + student.getId(), DELETE, studentHttpEntity, Student.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        deleteStudent(student.getId());
    }

    @Test
    void filterByAgeTest() throws Exception {
        List<Student> addedStudents = new ArrayList<>(List.of(newStudent(STUDENT), newStudent(STUDENT2), newStudent(STUDENT3)));
        List<Student> filteredStudents = addedStudents.stream()
                .filter(s -> s.getAge() == 24)
                .collect(Collectors.toList());

        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/filter?age=24", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(filteredStudents));
        addedStudents.forEach(s -> deleteStudent(s.getId()));
    }

    @Test
    void printAllTest() throws Exception {
        List<Student> addedStudents = new ArrayList<>(List.of(newStudent(STUDENT), newStudent(STUDENT2), newStudent(STUDENT3)));

        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(addedStudents));
        addedStudents.forEach(s -> deleteStudent(s.getId()));
    }

    @Test
    void findByAgeBetweenTest() throws Exception {
        List<Student> addedStudents = new ArrayList<>(List.of(newStudent(STUDENT), newStudent(STUDENT2), newStudent(STUDENT3)));
        List<Student> filteredStudents = addedStudents.stream()
                .filter(s -> s.getAge() > 20 && s.getAge() < 30)
                .collect(Collectors.toList());

        ResponseEntity<List> result =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/filter_range?startAge=20&endAge=30", List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mapper.writeValueAsString(result.getBody())).isEqualTo(mapper.writeValueAsString(filteredStudents));
        addedStudents.forEach(s -> deleteStudent(s.getId()));
    }

    @Test
    void getFaculty() {
        Student student = newStudent(STUDENT);
        ResponseEntity<Faculty> response =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + student.getId() + "/faculty", Faculty.class);
        Faculty facultyResponse = response.getBody();
        assertThat(facultyResponse).isEqualTo(student.getFaculty());
        ResponseEntity<Faculty> responseNull =
                testRestTemplate.getForEntity("http://localhost:" + port + "/student/" + -1 + "/faculty", Faculty.class);
        assertThat(responseNull.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        deleteStudent(student.getId());
    }

    @Test
    void getStudentsAmount() {
        Integer result =
                testRestTemplate.getForObject("http://localhost:" + port + "/student/students-count", Integer.class);
        assertThat(result.intValue()).isEqualTo(0);
        Student student = newStudent(STUDENT);
        result =
                testRestTemplate.getForObject("http://localhost:" + port + "/student/students-count", Integer.class);
        assertThat(result.intValue()).isEqualTo(1);
        deleteStudent(student.getId());
    }

    @Test
    void getAverageAge() {
        Student student1 = newStudent(STUDENT);
        Student student2 = newStudent(STUDENT2);
        Double result =
                testRestTemplate.getForObject("http://localhost:" + port + "/student/students-avg-age", Double.class);
        assertThat(result.doubleValue()).isEqualTo(37);
        deleteStudent(student1.getId());
        deleteStudent(student2.getId());

    }

    @Test
    void getFiveLastStudents() {
        List<Student> listOfStudents = new ArrayList<>(List.of(
                newStudent(new Student(2L, "Mock_name", 50, FACULTY)),
                newStudent(new Student(3L, "Mock_name", 50, FACULTY)),
                newStudent(new Student(4L, "Mock_name", 50, FACULTY)),
                newStudent(new Student(5L, "Mock_name", 50, FACULTY)),
                newStudent(new Student(6L, "Mock_name", 50, FACULTY))
        ));
        Collections.reverse(listOfStudents);
        Student mockStudent = newStudent(new Student(1L, "Mock_name", 50, FACULTY));

        List result =
                testRestTemplate.getForObject("http://localhost:" + port + "/student/students-five-last", List.class);
        assertThat(result).isEqualTo(listOfStudents);
        listOfStudents.forEach(e -> deleteStudent(e.getId()));
        deleteStudent(mockStudent.getId());
    }
}