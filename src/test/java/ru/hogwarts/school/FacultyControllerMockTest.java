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

    private final Faculty faculty = new Faculty("TestingName", "red", null);

    private final List<Faculty> faculties = new ArrayList<>(List.of(
            new Faculty("TestingName", "red", new ArrayList<>()),
            new Faculty("Mock_name", "red", new ArrayList<>())
    ));

    @Test
    public void contextTest() {
        assertThat(facultyController).isNotNull();
    }

    @Test
    public void getFacultyTest() throws Exception {
        JSONObject facultyRequest = new JSONObject();
        facultyRequest.put("name", "TestingName");
        facultyRequest.put("color", "red");

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        when(facultyRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", "1")
                        .content(facultyRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(faculty)));
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
        facultyRequest.put("name", "TestingName");
        facultyRequest.put("color", "red");

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(faculty)));
    }

    @Test
    public void updateFacultyTest() throws Exception {
        JSONObject facultyRequest = new JSONObject();
        facultyRequest.put("id", "1");
        facultyRequest.put("name", "TestingName");
        facultyRequest.put("color", "red");
        JSONObject facultyRequestNull = new JSONObject();
        facultyRequestNull.put("id", "222");

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        when(facultyRepository.findById(eq(222L))).thenReturn(Optional.empty());
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyRequest.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(faculty)));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyRequestNull.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));
        when(facultyRepository.findById(eq(222L))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(faculty)));
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/{id}", "222"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void filterByColorTest() throws Exception {
        when(facultyRepository.findByColorLikeIgnoreCase(any(String.class))).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filter?color=red"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(faculties)));
    }

    @Test
    public void printAllTest() throws Exception {
        when(facultyRepository.findAll()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(faculties)));
    }

    @Test
    public void findByNameIgnoreCaseOrColorIgnoreCaseTest() throws Exception {
        when(facultyRepository.findByNameContainsIgnoreCaseOrColorContainsIgnoreCase(any(String.class), any(String.class)))
                .thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/filterByColorOrName?nameOrColor=red"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(faculties)));
    }

    @Test
    public void getStudentsTest() throws Exception {
        List<Student> studentList = new ArrayList<>(List.of(
                new Student("Mock_name", 24, faculty),
                new Student("Mock_name", 24, faculty)
        ));
        faculty.setStudents(studentList);

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}/students", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(studentList)));
    }

    @Test
    public void getLongName() throws Exception {
        faculties.add(new Faculty("Loooooooong_Mock_name", "red", new ArrayList<>()));

        when(facultyRepository.findAll()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/long-name"))
                .andExpect(content().string("Loooooooong_Mock_name"));
    }
}