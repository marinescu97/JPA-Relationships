package com.spring.JpaRelationships.controller;

import com.spring.JpaRelationships.dto.CourseDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.service.CourseService;
import com.spring.JpaRelationships.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService service;
    private final EnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAll(){
        List<CourseDto> courses = service.findAll();
        return courses.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<StudentDto>> getStudents(@PathVariable Long courseId){
        List<StudentDto> students = service.findAllStudents(courseId);
        return students.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(students);
    }

    @PostMapping("/{courseId}/students")
    public ResponseEntity<String> addStudent(@PathVariable Long courseId,
                                                @RequestParam Long studentId){
        enrollmentService.enrollStudentToCourse(studentId, courseId);
        return ResponseEntity.ok("Student was added successfully.");
    }

    @DeleteMapping("/{courseId}/students")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long courseId,
                                             @RequestParam Long studentId){
        enrollmentService.unenrollStudentFromCourse(studentId, courseId);
        return ResponseEntity.noContent().build();
    }
}
