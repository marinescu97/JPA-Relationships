package com.spring.JpaRelationships.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.JpaRelationships.dto.AddressDto;
import com.spring.JpaRelationships.dto.AssignmentDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Assignment;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.mapper.AssignmentMapper;
import com.spring.JpaRelationships.mapper.StudentMapper;
import com.spring.JpaRelationships.repository.AssignmentRepository;
import com.spring.JpaRelationships.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AssignmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AssignmentMapper assignmentMapper;

    private Student testStudent;
    private List<Assignment> testAssignments;

    @BeforeEach
    void setUp() {
        StudentDto studentDto = StudentDto.builder()
                .name("Test student")
                .email("student@email.com")
                .address(AddressDto.builder()
                        .street("Test street")
                        .zipCode("1234")
                        .city("Test city")
                        .build())
                .build();

        testStudent = studentMapper.toEntity(studentDto);

        List<AssignmentDto> assignmentDtoList = List.of(
                new AssignmentDto("Assignment 1", LocalDate.of(2025, 8, 13)),
                new AssignmentDto("Assignment 2", LocalDate.of(2025, 8, 15)),
                new AssignmentDto("Assignment 3", LocalDate.of(2025, 8, 20))
        );

        testAssignments = assignmentDtoList.stream()
                .map(dto -> assignmentRepository.save(assignmentMapper.toEntity(dto, testStudent)))
                .toList();

        testStudent = studentRepository.save(testStudent);
    }

    @Test
    void getAllAssignments_shouldReturnAllAssignments() throws Exception{
        mockMvc.perform(get("/api/assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        List<Assignment> foundAssignments = assignmentRepository.findAll();
        assertEquals(3, foundAssignments.size());
        assertEquals(testAssignments.getFirst(), foundAssignments.getFirst());
        assertEquals(testAssignments.getLast(), foundAssignments.getLast());
    }

    @Test
    void getAllAssignments_shouldReturnEmptyList() throws Exception{
        assignmentRepository.deleteAll();

        mockMvc.perform(get("/api/assignments"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllByStudentId_validStudentId_shouldReturnAllAssignments() throws Exception {
        mockMvc.perform(get("/api/students/" + testStudent.getId() + "/assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        List<Assignment> foundAssignments = assignmentRepository.findAllByStudentId(testStudent.getId());
        assertEquals(testAssignments, foundAssignments);
    }

    @Test
    void getAllByStudentId_invalidStudentId_shouldReturnNoContent() throws Exception{
        mockMvc.perform(get("/api/students/" + any(Long.class) + "/assignments"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createAll_validStudentId_shouldSaveAssignmentList() throws Exception{
        List<AssignmentDto> dtoList = List.of(
                new AssignmentDto("New assignment 1", LocalDate.of(2025, 9, 12)),
                new AssignmentDto("New assignment 2", LocalDate.of(2025, 9, 11))
        );

        mockMvc.perform(post("/api/students/" + testStudent.getId() + "/assignments")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoList)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2));

        List<Assignment> allAssignments = assignmentRepository.findAll();

        assertEquals(5, allAssignments.size());
    }

    @Test
    void createAll_invalidStudentId_shouldReturnNotFound() throws Exception{
        List<AssignmentDto> dtoList = List.of(
                new AssignmentDto("New assignment 1", LocalDate.of(2025, 9, 12)),
                new AssignmentDto("New assignment 2", LocalDate.of(2025, 9, 11))
        );

        mockMvc.perform(post("/api/students/" + any(Long.class) + "/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dtoList)))
                .andExpect(status().isNotFound());

        List<Assignment> allAssignments = assignmentRepository.findAll();

        assertEquals(allAssignments, testAssignments);
    }

    @Test
    void deleteById_validId_shouldDeleteAssignment() throws Exception{
        Assignment assignment = testAssignments.getFirst();

        mockMvc.perform(delete("/api/assignments/" + assignment.getId()))
                .andExpect(status().isNoContent());

        testAssignments = assignmentRepository.findAll();

        assertFalse(testAssignments.contains(assignment));
    }

    @Test
    void deleteById_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/assignments/" + any(Long.class)))
                .andExpect(status().isNotFound());

        assertEquals(testAssignments, assignmentRepository.findAll());
    }

    @Test
    void deleteAllByStudentId_validStudentId_shouldDeleteAllAssignments() throws Exception {
        mockMvc.perform(delete("/api/students/" + testStudent.getId() + "/assignments"))
                .andExpect(status().isNoContent());

        assertTrue(assignmentRepository.findAll().isEmpty());
    }

    @Test
    void deleteAllByStudentId_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/students/" + any(Long.class) + "/assignments"))
                .andExpect(status().isNotFound());

        assertEquals(testAssignments, assignmentRepository.findAll());
    }

    @Test
    void updateAssignment_existent_shouldUpdateAssignment() throws Exception{
        Assignment assignment = testAssignments.getFirst();
        AssignmentDto dto = new AssignmentDto("Updated assignment", null);

        mockMvc.perform(put("/api/assignments/" + assignment.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assignment.getId()))
                .andExpect(jsonPath("$.title").value(dto.getTitle()))
                .andExpect(jsonPath("$.dueDate").doesNotExist());

        Optional<Assignment> updatedAssignment = assignmentRepository.findById(assignment.getId());

        assertTrue(updatedAssignment.isPresent());
        assertEquals(updatedAssignment.get().getTitle(), dto.getTitle());
        assertNull(updatedAssignment.get().getDueDate());
    }

    @Test
    void updateAssignment_nonExistent_shouldReturnNotFound() throws Exception{
        AssignmentDto dto = new AssignmentDto("Updated assignment", null);

        mockMvc.perform(put("/api/assignments/" + any(Long.class))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchAssignment_existent_shouldPerformPartialUpdate() throws Exception{
        Assignment assignment = testAssignments.getFirst();
        AssignmentDto dto = new AssignmentDto(null, LocalDate.now());

        mockMvc.perform(patch("/api/assignments/" + assignment.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpectAll(
                       jsonPath("$.id").value(assignment.getId()),
                       jsonPath("$.title").value(assignment.getTitle()),
                       jsonPath("$.dueDate").value(dto.getDueDate().toString())
                );

        Optional<Assignment> updatedAssignment = assignmentRepository.findById(assignment.getId());

        assertTrue(updatedAssignment.isPresent());
        assertEquals(updatedAssignment.get().getTitle(), assignment.getTitle());
        assertEquals(updatedAssignment.get().getDueDate(), dto.getDueDate());
    }

    @Test
    void patchAssignment_nonExistent_shouldReturnNotFound() throws Exception{
        AssignmentDto dto = new AssignmentDto(testAssignments.getFirst().getTitle(), testAssignments.getFirst().getDueDate());

        mockMvc.perform(patch("/api/assignments/" + any(Long.class))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}