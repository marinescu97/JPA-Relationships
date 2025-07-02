package com.spring.JpaRelationships.service;

import com.spring.JpaRelationships.dto.CourseDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Course;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.exception.ResourceNotFoundException;
import com.spring.JpaRelationships.mapper.StudentMapper;
import com.spring.JpaRelationships.repository.CourseRepository;
import com.spring.JpaRelationships.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
class CourseServiceIT {
    private final CourseService service;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    private Student testStudent;
    private List<Course> testCourses;

    @BeforeEach
    void setUp() {
        StudentDto studentDto = StudentDto.builder()
                .name("Test student")
                .email("student@email.com")
                .build();

        testCourses = courseRepository.findAll();

        testStudent = studentMapper.toEntity(studentDto);

        testStudent.getCourses().addAll(testCourses);
        studentRepository.save(testStudent);

        testCourses.forEach(this::addStudentAndSave);

        testCourses = courseRepository.findAll();
    }

    private void addStudentAndSave(Course course){
        course.getStudents().add(testStudent);
        courseRepository.save(course);
    }

    @Test
    void findById_validId_shouldReturnCourse() {
        Course courseToFind = testCourses.get(1);

        Course foundCourse = service.findById(courseToFind.getId());

        assertEquals(courseToFind, foundCourse);
    }

    @Test
    void findById_invalidId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.findById(any(Long.class)));
    }

    @Test
    void findAll_shouldReturnCourseList() {
        List<CourseDto> foundCourses = service.findAll();

        assertEquals(3, foundCourses.size());
        assertEquals(testCourses.getFirst().getCode(), foundCourses.getFirst().getCode());
        assertEquals(testCourses.getLast().getCode(), foundCourses.getLast().getCode());
    }

    @Test
    void findAll_shouldReturnEmptyList() {
        courseRepository.deleteAll();
        testStudent.getCourses().clear();
        studentRepository.save(testStudent);

        List<CourseDto> foundCourses = service.findAll();
        System.out.println(foundCourses);

        assertTrue(foundCourses.isEmpty());
    }

    @Test
    void findAllStudents_shouldReturnStudent() {
        List<StudentDto> foundStudents = service.findAllStudents(testCourses.getFirst().getId());

        assertEquals(1, foundStudents.size());
        assertEquals(testStudent.getName(), foundStudents.getFirst().getName());
    }

    @Test
    void findAllStudents_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.findAllStudents(any(Long.class)));
    }
}