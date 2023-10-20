package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class)
class StudentControllerMockTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentRepository studentRepository;
    @SpyBean
    private StudentService studentService;
    @InjectMocks
    private StudentController studentController;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Student student = new Student("TestingName", 24, null);
    private final List<Student> students = new ArrayList<>(List.of(
            new Student("TestingName", 10, null),
            new Student("TestingName", 20, null)));

    @Test
    public void contextTest() {
        assertThat(studentController).isNotNull();
    }

    @Test
    public void postStudentTest() throws Exception {
        JSONObject studentRequest = new JSONObject();
        studentRequest.put("name", "TestingName");
        studentRequest.put("age", "24");

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(student)));
    }

    @Test
    public void getStudentTest() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(student)));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/{id}", "222")).andExpect(status().isNotFound());
    }

    @Test
    public void updateStudentTest() throws Exception {
        JSONObject studentRequest = new JSONObject();
        studentRequest.put("id", "1");
        studentRequest.put("name", "TestingName");
        studentRequest.put("age", "24");

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(student)));

        JSONObject wrongStudent = new JSONObject();
        wrongStudent.put("id", "222");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(wrongStudent.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteStudentTest() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(student)));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", "222"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void filterByAgeTest() throws Exception {
        when(studentRepository.findByAge(anyInt())).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filter?age=24"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(students)));
    }

    @Test
    public void printAllTest() throws Exception {
        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(students)));
    }

    @Test
    public void findByAgeBetweenTest() throws Exception {
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filter_range?startAge=5&endAge=10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(students)));
    }

    @Test
    public void getFacultyTest() throws Exception {
        Faculty faculty = new Faculty("TestingName", "red", null);
        student.setFaculty(faculty);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));
        when(studentRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}/faculty", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(faculty)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}/faculty", "222"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStudentsAmount() throws Exception {
        when(studentRepository.getStudentsAmount()).thenReturn(4);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/students-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("4"));
    }

    @Test
    void getAverageAge() throws Exception {
        when(studentRepository.getAverageAge()).thenReturn(37.22);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/students-avg-age"))
                .andExpect(status().isOk())
                .andExpect(content().string("37.22"));
    }

    @Test
    void findByNameSorted() throws Exception {
        List<Student> listOfStudents = new ArrayList<>(List.of(
                new Student("aifth__2", 10, null),
                new Student("aecond__3", 20, null),
                new Student("Azex__5", 30, null),
                new Student("Alex2__4", 40, null),
                new Student("Tenth__8", 50, null)));
        List<String> expectedList = new ArrayList<>(List.of("AECOND__3", "AIFTH__2", "ALEX2__4", "AZEX__5"));

        when(studentRepository.findAll()).thenReturn(listOfStudents);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/sorted-name"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedList.toString()));
    }

    @Test
    void findAverageAge() throws Exception {
        when(studentRepository.findAll()).thenReturn(students);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/average-age"))
                .andExpect(status().isOk())
                .andExpect(content().string("15.0"));
    }

    @Test
    void getIntNumber() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/intNumber"))
                .andExpect(status().isOk())
                .andExpect(content().string("1784293664"));
    }
}