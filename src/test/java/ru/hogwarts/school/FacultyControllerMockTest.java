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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.hogwarts.school.TestConstants.*;

@WebMvcTest(controllers = FacultyController.class)
class FacultyControllerMockTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FacultyRepository facultyRepository;
    @SpyBean
    private FacultyService facultyService;
    @InjectMocks
    private FacultyController facultyController;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void contextTest() {
        assertThat(facultyController).isNotNull();
    }

    @Test
    public void getFacultyTest() throws Exception {
        JSONObject facultyRequest = new JSONObject();
        facultyRequest.put("name", NAME_CONSTANT);
        facultyRequest.put("color", COLOR_CONSTANT);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(FACULTY));
        when(facultyRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", "1")
                        .content(facultyRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(FACULTY)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", "222")
                        .content(facultyRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postFacultyTest() throws Exception {
        JSONObject facultyRequest = new JSONObject();
        facultyRequest.put("name", NAME_CONSTANT);
        facultyRequest.put("color", COLOR_CONSTANT);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(FACULTY);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(FACULTY)));
    }

    @Test
    public void updateFacultyTest() throws Exception {
        JSONObject facultyRequest = new JSONObject();
        facultyRequest.put("id", DB_ID_CONSTANT);
        facultyRequest.put("name", NAME_CONSTANT);
        facultyRequest.put("color", COLOR_CONSTANT);
        JSONObject facultyRequestNull = new JSONObject();
        facultyRequestNull.put("id", "222");

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(FACULTY));
        when(facultyRepository.findById(eq(222L))).thenReturn(Optional.empty());
        when(facultyRepository.save(any(Faculty.class))).thenReturn(FACULTY);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(FACULTY)));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyRequestNull.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(FACULTY));
        when(facultyRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(FACULTY)));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", "222"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void filterByColorTest() throws Exception {
        when(facultyRepository.findByColorLikeIgnoreCase(any(String.class))).thenReturn(COLLECTION_OF_FACULTIES);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter?color=red"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COLLECTION_OF_FACULTIES)));
    }

    @Test
    public void printAllTest() throws Exception {
        when(facultyRepository.findAll()).thenReturn(COLLECTION_OF_FACULTIES);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COLLECTION_OF_FACULTIES)));
    }

    @Test
    public void findByNameIgnoreCaseOrColorIgnoreCaseTest() throws Exception {
        when(facultyRepository.findByNameContainsIgnoreCaseOrColorContainsIgnoreCase(any(String.class), any(String.class)))
                .thenReturn(COLLECTION_OF_FACULTIES);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filterByColorOrName?nameOrColor=red"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COLLECTION_OF_FACULTIES)));
    }

    @Test
    public void getStudentsTest() throws Exception {
        Faculty faculty = new Faculty(1L, "TestingName", "red", null);
        List<Student> studentList= new ArrayList<>(List.of(
                new Student(1L, "Mock_name", 24, faculty),
                new Student(2L, "Mock_name", 24, faculty)
        ));
        faculty.setStudents(studentList);
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}/students", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(studentList)));
    }
}