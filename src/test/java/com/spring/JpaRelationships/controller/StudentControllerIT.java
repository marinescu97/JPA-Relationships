package com.spring.JpaRelationships.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.JpaRelationships.dto.AddressDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Course;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.mapper.StudentMapper;
import com.spring.JpaRelationships.repository.CourseRepository;
import com.spring.JpaRelationships.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class StudentControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private StudentMapper studentMapper;

    private Student testStudent;
    private static StudentDto studentDto;
    private static Course course;

    @BeforeAll
    static void beforeAll() {
        AddressDto addressDto = AddressDto.builder()
                .street("Test street")
                .zipCode("1234")
                .city("City")
                .build();

        studentDto = StudentDto.builder()
                .name("Test student")
                .email("student@email.com")
                .address(addressDto)
                .build();
    }

    @BeforeEach
    void setUp() {
        testStudent = studentRepository.save(studentMapper.toEntity(studentDto));
        course = courseRepository.findAll().getFirst();
    }

    @Test
    void createStudent_shouldSaveStudent() throws Exception {
        studentRepository.deleteAll();

        mockMvc.perform(post("/api/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(studentDto)))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.name").value(studentDto.getName()),
                        jsonPath("$.email").value(studentDto.getEmail())
                );

        Student student = studentRepository.findAll().getFirst();

        assertEquals(studentDto.getName(), student.getName());
        assertEquals(studentDto.getEmail(), student.getEmail());
        assertEquals(studentDto.getAddress().getStreet(), student.getAddress().getStreet());
    }

    @Test
    void addCourse_validData_shouldAddCourse() throws Exception{
        mockMvc.perform(post("/api/students/" + testStudent.getId() + "/courses")
                .param("courseId", course.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Course was successfully added."));

        Student student = studentRepository.findAll().getFirst();
        course = courseRepository.findAll().getFirst();

        assertTrue(student.getCourses().contains(course));
        assertTrue(course.getStudents().contains(student));
    }

    @Test
    void addCourse_invalidStudentId_shouldReturnNotFound() throws Exception{
        long studentId = 100L;
        mockMvc.perform(post("/api/students/" + studentId + "/courses")
                .param("courseId", course.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found with id " + studentId));
    }

    @Test
    void addCourse_invalidCourseId_shouldReturnNotFound() throws Exception{
        long courseId = 100L;

        mockMvc.perform(post("/api/students/" + testStudent.getId() + "/courses")
                        .param("courseId", String.valueOf(courseId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));
    }

    @Test
    void getAll_shouldReturnStudentList() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        List<Student> foundStudents = studentRepository.findAll();

        assertEquals(1, foundStudents.size());
        assertEquals(testStudent, foundStudents.getFirst());
    }

    @Test
    void getAll_shouldReturnNoContent() throws Exception {
        studentRepository.deleteAll();

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isNoContent());

        List<Student> foundStudents = studentRepository.findAll();

        assertTrue(foundStudents.isEmpty());
    }

    @Test
    void getStudent_validId_shouldReturnStudent() throws Exception {
        mockMvc.perform(get("/api/students/" + testStudent.getId()))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(testStudent.getId()),
                        jsonPath("$.name").value(testStudent.getName()),
                        jsonPath("$.email").value(testStudent.getEmail())
                );

        assertTrue(studentRepository.findById(testStudent.getId()).isPresent());
    }

    @Test
    void getStudent_invalidId_shouldReturnNotFound() throws Exception {
        long studentId = 100L;

        mockMvc.perform(get("/api/students/", studentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCourses_shouldReturnAllCourses() throws Exception {
        List<Course> courses = courseRepository.findAll();
        testStudent.getCourses().addAll(courses);
        testStudent = studentRepository.save(testStudent);

        mockMvc.perform(get("/api/students/" + testStudent.getId() + "/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        assertEquals(courses, testStudent.getCourses().stream().toList());
    }

    @Test
    void getCourses_shouldReturnNoContent() throws Exception {
        mockMvc.perform(get("/api/students/" + testStudent.getId() + "/courses"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteStudent_existent_shouldDeleteStudent() throws Exception {
        mockMvc.perform(delete("/api/students/" + testStudent.getId()))
                .andExpect(status().isNoContent());

        assertTrue(studentRepository.findAll().isEmpty());
    }

    @Test
    void deleteStudent_nonExistent_shouldReturnNotFound() throws Exception {
        long studentId = 100L;

        mockMvc.perform(delete("/api/students/" + studentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found with id " + studentId));

        assertEquals(1, studentRepository.findAll().size());
    }

    @Test
    void deleteCourse_existent_shouldDeleteCourse() throws Exception {
        testStudent.getCourses().add(course);
        testStudent = studentRepository.save(testStudent);

        course.getStudents().add(testStudent);
        course = courseRepository.save(course);

        mockMvc.perform(delete("/api/students/" + testStudent.getId() + "/courses")
                .param("courseId", course.getId().toString()))
                .andExpect(status().isNoContent());

        assertEquals(3, courseRepository.findAll().size());
        assertTrue(studentRepository.findById(testStudent.getId()).get().getCourses().isEmpty());
    }

    @Test
    void deleteCourse_nonExistent_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/students/" + testStudent.getId() + "/courses")
                .param("courseId", "100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Course not found"));
    }

    @Test
    void updateStudent_existent_shouldUpdateStudent() throws Exception {
        studentDto.setName("Student 2");
        studentDto.setEmail(null);

        mockMvc.perform(put("/api/students/" + testStudent.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(studentDto)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(testStudent.getId()),
                        jsonPath("$.name").value(studentDto.getName()),
                        jsonPath("$.email").doesNotExist()
                );

        testStudent = studentRepository.findAll().getFirst();

        assertEquals(studentDto.getName(), testStudent.getName());
        assertNull(testStudent.getEmail());
    }

    @Test
    void updateStudent_nonExistent_shouldReturnNotFound() throws Exception{
        long studentId = 100L;

        mockMvc.perform(put("/api/students/", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(studentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchStudent_existent_shouldPerformPartialUpdate() throws Exception{
        studentDto.setName("Student 2");
        studentDto.setEmail(null);

        mockMvc.perform(patch("/api/students/" + testStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(studentDto)))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(testStudent.getId()),
                        jsonPath("$.name").value(studentDto.getName()),
                        jsonPath("$.email").value(testStudent.getEmail())
                );

        testStudent = studentRepository.findAll().getFirst();

        assertEquals(studentDto.getName(), testStudent.getName());
        assertNotNull(testStudent.getEmail());
    }

    @Test
    void patchStudent_nonExistent_shouldReturnNotFound() throws Exception{
        long studentId = 100L;
        mockMvc.perform(patch("/api/students/" + studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(studentDto)))
                .andExpect(status().isNotFound());
    }
}