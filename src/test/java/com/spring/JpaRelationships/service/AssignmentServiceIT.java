package com.spring.JpaRelationships.service;

import com.spring.JpaRelationships.dto.AddressDto;
import com.spring.JpaRelationships.dto.AssignmentDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Assignment;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.exception.ResourceNotFoundException;
import com.spring.JpaRelationships.mapper.AssignmentMapper;
import com.spring.JpaRelationships.mapper.StudentMapper;
import com.spring.JpaRelationships.repository.AssignmentRepository;
import com.spring.JpaRelationships.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
class AssignmentServiceIT {
    private final AssignmentService service;

    private final AssignmentRepository repository;

    private final StudentRepository studentRepository;

    private final AssignmentMapper assignmentMapper;

    private final StudentMapper studentMapper;

    private List<Assignment> testAssignmentList;

    private static List<AssignmentDto> assignmentDtoList;

    private Student testStudent;

    @BeforeAll
    static void beforeAll() {
        assignmentDtoList = List.of(
                new AssignmentDto("Assignment 1", LocalDate.of(2025, 8, 9)),
                new AssignmentDto("Assignment 2", LocalDate.of(2025, 7, 15)),
                new AssignmentDto("Assignment 3", LocalDate.of(2025, 8, 12))
        );
    }

    @BeforeEach
    void setUp() {
        AddressDto addressDto = AddressDto.builder()
                .street("My street")
                .zipCode("1234")
                .city("Test city")
                .build();

        StudentDto studentDto = StudentDto.builder()
                .name("Student name")
                .email("student@email.com")
                .address(addressDto)
                .build();

        testStudent = studentMapper.toEntity(studentDto);
        testStudent = studentRepository.save(testStudent);

        testAssignmentList = assignmentDtoList.stream()
                .map(dto -> assignmentMapper.toEntity(dto, testStudent))
                .toList();

        repository.saveAll(testAssignmentList);
    }

    @Test
    void findAll_shouldReturnAllAssignments() {
        List<Assignment> assignments = service.findAll();

        assertEquals(3, assignments.size());
        assertEquals(testAssignmentList.getFirst(), assignments.getFirst());
        assertEquals(testAssignmentList.getLast(), assignments.getLast());
    }

    @Test
    void findAllByStudentId_existingId_shouldReturnAllAssignments() {
        List<Assignment> assignments = service.findAllByStudentId(testStudent.getId());

        assertEquals(3, assignments.size());
        assertEquals(testAssignmentList.getFirst(), assignments.getFirst());
        assertEquals(testAssignmentList.getLast(), assignments.getLast());
    }

    @Test
    void findAllByStudentId_nonExistingId_shouldReturnEmptyList() {
        List<Assignment> foundAssignments = service.findAllByStudentId(99L);

        assertTrue(foundAssignments.isEmpty());
    }

    @Test
    void saveAll_existingStudentId_shouldSaveAllAssignments() {
        repository.deleteAll();

        List<Assignment> savedAssignments = service.saveAll(testStudent.getId(), assignmentDtoList);

        assertEquals(3, savedAssignments.size());
    }

    @Test
    void saveAll_nonExistingStudentId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.saveAll(20L, assignmentDtoList));
    }

    @Test
    void deleteById_existingId_shouldDeleteAssignment() {
        service.deleteById(testAssignmentList.getFirst().getId());

        List<Assignment> remainingAssignments = service.findAll();

        assertEquals(2, remainingAssignments.size());
        assertEquals(testAssignmentList.get(1), remainingAssignments.getFirst());
    }

    @Test
    void deleteById_nonExistingId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.deleteById(20L));
    }

    @Test
    void deleteAllByStudentId_existingId_shouldDeleteAllAssignments() {
        service.deleteAllByStudentId(testStudent.getId());

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void deleteAllByStudentId_nonExistingId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.deleteAllByStudentId(3L));
    }

    @Test
    void updateAssignment_existing_shouldPerformUpdate() {
        String title = "Updated Assignment";
        Assignment updatedAssignment = service.updateAssignment(testAssignmentList.getFirst().getId(), new AssignmentDto(title, null));

        testAssignmentList = repository.findAll();

        assertEquals(title, updatedAssignment.getTitle());
        assertEquals(title, testAssignmentList.getFirst().getTitle());
        assertNull(testAssignmentList.getFirst().getDueDate());
    }

    @Test
    void updateAssignment_nonExisting_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.updateAssignment(100L, any(AssignmentDto.class)));
    }

    @Test
    void patchAssignment_existing_shouldPerformPartialUpdate() {
        String title = "Updated assignment";
        Assignment updatedAssignment = service.patchAssignment(1L, new AssignmentDto(title, null));

        testAssignmentList = repository.findAll();

        assertEquals(title, updatedAssignment.getTitle());
        assertEquals(title, testAssignmentList.getFirst().getTitle());
        assertNotNull(testAssignmentList.getFirst().getDueDate());
    }

    @Test
    void patchAssignment_nonExisting_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.patchAssignment(10L, any(AssignmentDto.class)));
    }
}