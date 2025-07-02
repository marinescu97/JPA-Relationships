package com.spring.JpaRelationships.controller;

import com.spring.JpaRelationships.dto.AddressDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Course;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.mapper.StudentMapper;
import com.spring.JpaRelationships.repository.CourseRepository;
import com.spring.JpaRelationships.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentMapper studentMapper;

    private List<Course> testCourses;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        testCourses = courseRepository.findAll();

        AddressDto addressDto = AddressDto.builder()
                .street("Test street")
                .zipCode("1234")
                .city("City")
                .build();

        StudentDto studentDto = StudentDto.builder()
                .name("Test student")
                .email("student@email.com")
                .address(addressDto)
                .build();

        testStudent = studentMapper.toEntity(studentDto);
        testStudent.getCourses().addAll(testCourses);
        testStudent = studentRepository.save(testStudent);

        testCourses = testCourses.stream()
                .map(this::addStudentToCourse)
                .toList();
    }

    private Course addStudentToCourse(Course course){
        course.getStudents().add(testStudent);
        return courseRepository.save(course);
    }

    @Test
    void getAll_shouldReturnAllCourses() throws Exception{
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(testCourses.size()));
    }

    @Test
    void getStudents_shouldReturnStudentList() throws Exception{
        Course course = courseRepository.findAll().getFirst();
        mockMvc.perform(get("/api/courses/" + course.getId() + "/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        course = courseRepository.findById(course.getId()).orElseThrow();

        assertTrue(course.getStudents().contains(testStudent));
    }

    @Test
    void getStudents_shouldReturnNoContent() throws Exception{
        Course course = courseRepository.findAll().getFirst();
        course.getStudents().clear();
        course = courseRepository.findById(course.getId()).orElseThrow();

        mockMvc.perform(get("/api/courses/" + course.getId() + "/students"))
                .andExpect(status().isNoContent());
    }

    @Test
    void addStudent_validCourseId_shouldAddStudentToCourse() throws Exception{
        StudentDto dto = StudentDto.builder()
                .name("Student dto")
                .email("student@email.com")
                .build();
        Student student = studentRepository.save(studentMapper.toEntity(dto));
        Course course = testCourses.getFirst();

        mockMvc.perform(post("/api/courses/" + course.getId() + "/students")
                .param("studentId", student.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("Student was added successfully."));

        student = studentRepository.findById(student.getId()).orElseThrow();
        course = courseRepository.findById(course.getId()).orElseThrow();

        assertTrue(student.getCourses().contains(course));
        assertTrue(course.getStudents().contains(student));
    }

    @Test
    void addStudent_invalidCourseId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/courses/" + any(Long.class) + "/students")
                .param("studentId", any(Long.class).toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addStudent_invalidStudentId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/courses/" + testCourses.getFirst().getId() + "/students")
                .param("studentId", any(Long.class).toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_validCourseAndStudent_shouldDeleteStudent() throws Exception{
        Course course = testCourses.getFirst();

        mockMvc.perform(delete("/api/courses/" + course.getId() + "/students")
                .param("studentId", testStudent.getId().toString()))
                .andExpect(status().isNoContent());

        course = courseRepository.findById(course.getId()).orElseThrow();
        testStudent = studentRepository.findById(testStudent.getId()).orElseThrow();

        assertFalse(course.getStudents().stream()
                .anyMatch(student -> student.getId().equals(testStudent.getId())));
        assertFalse(testStudent.getCourses().contains(course));
    }

    @Test
    void deleteStudent_invalidCourse_shouldReturnNotFound() throws Exception{
        mockMvc.perform(delete("/api/courses/" + any(Long.class) + "/students")
                        .param("studentId", testStudent.getId().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_invalidStudent_shouldReturnNotFound() throws Exception{
        Course course = testCourses.getFirst();

        mockMvc.perform(delete("/api/courses/" + course.getId() + "/students")
                        .param("studentId", any(Long.class).toString()))
                .andExpect(status().isNotFound());
    }
}