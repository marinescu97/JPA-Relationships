package com.spring.JpaRelationships.service;

import com.spring.JpaRelationships.entity.Course;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.exception.ResourceNotFoundException;
import com.spring.JpaRelationships.exception.UniqueFieldException;
import com.spring.JpaRelationships.repository.CourseRepository;
import com.spring.JpaRelationships.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class EnrollmentService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }

    private Course findCourse(Long id){
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    @Transactional
    public void enrollStudentToCourse(Long studentId, Long courseId) {
        Student student = findStudent(studentId);
        Course course = findCourse(courseId);

        if (student.getCourses().add(course) && course.getStudents().add(student)){
            saveStudentAndCourse(student, course);
        } else {
            throw new UniqueFieldException("Course already exist for this student.");
        }
    }

    private void saveStudentAndCourse(Student student, Course course){
        studentRepository.save(student);
        courseRepository.save(course);
    }

    @Transactional
    public void unenrollStudentFromCourse(Long studentId, Long courseId) {
        Course course = findCourse(courseId);
        Student student = findStudent(studentId);

        if (student.getCourses().remove(course) && course.getStudents().remove(student)){
            saveStudentAndCourse(student, course);
        } else {
            throw new ResourceNotFoundException("This course doesn't contain student with id " + studentId);
        }
    }
}
