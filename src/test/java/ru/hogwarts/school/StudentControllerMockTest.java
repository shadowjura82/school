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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.hogwarts.school.TestConstants.*;

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

    @Test
    public void contextTest() {
        assertThat(studentController).isNotNull();
    }

    @Test
    public void postStudentTest() throws Exception {
        JSONObject studentRequest = new JSONObject();
        studentRequest.put("name", NAME_CONSTANT);
        studentRequest.put("age", AGE_CONSTANT);

        when(studentRepository.save(any(Student.class))).thenReturn(STUDENT);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME_CONSTANT))
                .andExpect(jsonPath("$.age").value(AGE_CONSTANT))
                .andExpect(jsonPath("$.id").value(DB_ID_CONSTANT));
    }

    @Test
    public void getStudentTest() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(STUDENT));
        when(studentRepository.findById(eq(222L))).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME_CONSTANT))
                .andExpect(jsonPath("$.id").value(DB_ID_CONSTANT))
                .andExpect(jsonPath("$.age").value(AGE_CONSTANT));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/{id}", "222")).andExpect(status().isNotFound());
    }

    @Test
    public void updateStudentTest() throws Exception {
        JSONObject studentRequest = new JSONObject();
        studentRequest.put("id", DB_ID_CONSTANT);
        studentRequest.put("name", NAME_CONSTANT);
        studentRequest.put("age", AGE_CONSTANT);

        when(studentRepository.save(any(Student.class))).thenReturn(STUDENT);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(STUDENT));
        when(studentRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(studentRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME_CONSTANT))
                .andExpect(jsonPath("$.age").value(AGE_CONSTANT))
                .andExpect(jsonPath("$.id").value(DB_ID_CONSTANT));

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
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(STUDENT));
        when(studentRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(NAME_CONSTANT))
                .andExpect(jsonPath("$.age").value(AGE_CONSTANT))
                .andExpect(jsonPath("$.id").value(DB_ID_CONSTANT));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/{id}", "222"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void filterByAgeTest() throws Exception {
        when(studentRepository.findByAge(anyInt())).thenReturn(COLLECTION_OF_STUDENTS);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filter?age=50"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COLLECTION_OF_STUDENTS)));
    }

    @Test
    public void printAllTest() throws Exception {
        when(studentRepository.findAll()).thenReturn(COLLECTION_OF_STUDENTS);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COLLECTION_OF_STUDENTS)));
    }

    @Test
    public void findByAgeBetweenTest() throws Exception {
        when(studentRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(COLLECTION_OF_STUDENTS);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/filter_range?startAge=5&endAge=10"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COLLECTION_OF_STUDENTS)));
    }

    @Test
    public void getFacultyTest() throws Exception {
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(STUDENT));
        when(studentRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/{id}/faculty", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(FACULTY)));
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
    void getFiveLastStudents() {
        List<Student> listOfStudents = new ArrayList<>(List.of(
                new Student(2L, "Mock_name", 50, FACULTY),
                new Student(3L, "Mock_name", 50, FACULTY),
                new Student(4L, "Mock_name", 50, FACULTY),
                new Student(5L, "Mock_name", 50, FACULTY),
                new Student(6L, "Mock_name", 50, FACULTY)
        ));
//        when(studentRepository.getFiveLastStudents()).thenReturn(listOfStudents);

//        Нужно узнать как сделать этот тест
    }
}