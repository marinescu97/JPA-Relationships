package com.spring.JpaRelationships.service;

import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Course;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.exception.ResourceNotFoundException;
import com.spring.JpaRelationships.exception.UniqueFieldException;
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
class EnrollmentServiceIT {
    private final EnrollmentService service;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentMapper studentMapper;

    private Student testStudent;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        StudentDto studentDto = StudentDto.builder()
                .name("Test student")
                .email("student@email.com")
                .build();

        testStudent = studentRepository.save(studentMapper.toEntity(studentDto));
        testCourse = courseRepository.findAll().getFirst();
    }

    @Test
    void enrollStudentToCourse_validData_shouldPerformEnrollment() {
        service.enrollStudentToCourse(testStudent.getId(), testCourse.getId());
        testCourse = courseRepository.findById(testCourse.getId()).get();

        assertTrue(testStudent.getCourses().contains(testCourse));
        assertTrue(testCourse.getStudents().contains(testStudent));
    }

    @Test
    void enrollStudentToCourse_invalidStudentId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.enrollStudentToCourse(any(Long.class), testCourse.getId()));
    }

    @Test
    void enrollStudentToCourse_invalidCourseId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.enrollStudentToCourse(testStudent.getId(), any(Long.class)));
    }

    @Test
    void enrollStudentToCourse_duplicateCourse_shouldThrowException() {
        testStudent.getCourses().add(testCourse);
        studentRepository.save(testStudent);

        UniqueFieldException exception = assertThrows(
                UniqueFieldException.class,
                () -> service.enrollStudentToCourse(testStudent.getId(), testCourse.getId()));

        assertEquals("Course already exist for this student.", exception.getMessage());
    }

    private void addStudentToCourses(){
        List<Course> courses = courseRepository.findAll();
        testStudent.getCourses().addAll(courses);
        studentRepository.save(testStudent);

        courses.forEach(course -> {
            course.getStudents().add(testStudent);
            courseRepository.save(course);
        });
    }

    @Test
    void unenrollStudentFromCourse_validData_shouldPerformDeletion() {
        addStudentToCourses();

        service.unenrollStudentFromCourse(testStudent.getId(), testCourse.getId());
        testCourse = courseRepository.findAll().getFirst();

        assertEquals(2, testStudent.getCourses().size());
        assertTrue(testCourse.getStudents().isEmpty());
    }

    @Test
    void unenrollStudentFromCourse_invalidStudentId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.unenrollStudentFromCourse(any(Long.class), testCourse.getId()));
    }

    @Test
    void unenrollStudentFromCourse_invalidCourseId_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.unenrollStudentFromCourse(testStudent.getId(), any(Long.class)));
    }

    @Test
    void unenrollStudentFromCourse_nonExistingCourse_shouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> service.unenrollStudentFromCourse(testStudent.getId(), testCourse.getId()));
    }
}