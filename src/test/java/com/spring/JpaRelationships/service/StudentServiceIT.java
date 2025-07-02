package com.spring.JpaRelationships.service;

import com.spring.JpaRelationships.dto.AddressDto;
import com.spring.JpaRelationships.dto.CourseDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.exception.ResourceNotFoundException;
import com.spring.JpaRelationships.mapper.StudentMapper;
import com.spring.JpaRelationships.repository.CourseRepository;
import com.spring.JpaRelationships.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentServiceIT {
    @Autowired
    private StudentService service;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentMapper mapper;

    private Student testStudent;
    private static StudentDto testDto;

    @BeforeAll
    static void beforeAll() {
        testDto = StudentDto.builder()
                .name(null)
                .email("student@email.com")
                .address(null)
                .build();
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

        testStudent = mapper.toEntity(studentDto);
        testStudent.getCourses().addAll(courseRepository.findAll());

        testStudent = studentRepository.save(testStudent);
    }

    @Test
    void testSave_shouldSaveStudent() {
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

        Student savedStudent = service.save(studentDto);

        assertNotNull(savedStudent.getId());
        assertEquals(studentDto.getName(), savedStudent.getName());
    }

    @Test
    void testFindAll_shouldReturnAllStudents() {
        List<Student> foundStudents = service.findAll();

        assertEquals(1, foundStudents.size());
        assertEquals(testStudent.getName(), foundStudents.getFirst().getName());
        assertEquals(testStudent.getAddress(), foundStudents.getFirst().getAddress());
    }

    @Test
    void testFindAll_shouldReturnEmptyList() {
        studentRepository.deleteAll();

        List<Student> foundStudents = service.findAll();

        assertTrue(foundStudents.isEmpty());
    }

    @Test
    void testFindById_shouldReturnStudent() {
        Student foundStudent = service.findById(testStudent.getId());

        assertEquals(foundStudent, testStudent);
    }

    @Test
    void testFindById_shouldThrowException() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.findById(100L));
        assertEquals("Student not found with id 100", ex.getMessage());
    }

    @Test
    void testFindAllCourses_shouldReturnCourses() {
        List<CourseDto> foundCourses = service.findAllCourses(testStudent.getId());

        assertEquals(3, foundCourses.size());
    }

    @Test
    void testFindAllCourses_shouldReturnEmptyList() {
        testStudent.getCourses().clear();
        studentRepository.save(testStudent);

        List<CourseDto> foundCourses = service.findAllCourses(testStudent.getId());

        assertTrue(foundCourses.isEmpty());
    }

    @Test
    void testFindAllCourses_shouldThrowException() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.findAllCourses(100L));
        assertEquals("Student not found with id 100", ex.getMessage());
    }

    @Test
    void testDeleteById_shouldDeleteStudent() {
        service.deleteById(testStudent.getId());

        assertTrue(studentRepository.findAll().isEmpty());
    }

    @Test
    void testDeleteById_shouldThrowException() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.deleteById(100L));
        assertEquals("Student not found with id 100", ex.getMessage());
    }

    @Test
    void testUpdateById_shouldUpdateStudent() {
        service.updateById(testStudent.getId(), testDto);
        Optional<Student> updatedStudent = studentRepository.findById(testStudent.getId());

        assertTrue(updatedStudent.isPresent());
        assertNull(updatedStudent.get().getName());
        assertEquals(testDto.getEmail(), updatedStudent.get().getEmail());
        assertNull(updatedStudent.get().getAddress());
    }

    @Test
    void testUpdateById_shouldThrowException() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.updateById(100L, any(StudentDto.class)));
        assertEquals("Student not found with id 100", ex.getMessage());
    }

    @Test
    void testPatchById_shouldUpdateStudent() {
        service.patchById(testStudent.getId(), testDto);
        Optional<Student> updatedStudent = studentRepository.findById(testStudent.getId());

        assertTrue(updatedStudent.isPresent());
        assertEquals(testDto.getEmail(), updatedStudent.get().getEmail());
        assertNotNull(updatedStudent.get().getAddress());
    }

    @Test
    void testPatchById_shouldThrowException() {
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.patchById(100L, any(StudentDto.class)));
        assertEquals("Student not found with id 100", ex.getMessage());
    }
}