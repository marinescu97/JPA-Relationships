package com.spring.JpaRelationships.controller;

import com.spring.JpaRelationships.dto.CourseDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.service.EnrollmentService;
import com.spring.JpaRelationships.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody StudentDto dto){
        Student savedStudent = service.save(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @PostMapping("/{studentId}/courses")
    public ResponseEntity<String> addCourse(@PathVariable Long studentId,
                                            @RequestParam Long courseId){
        enrollmentService.enrollStudentToCourse(studentId, courseId);
        return ResponseEntity.ok("Course was successfully added.");
    }

    @GetMapping
    public ResponseEntity<?> getAll(){
        List<Student> students = service.findAll();

        return students.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id){
        Student foundStudent = service.findById(id);

        return ResponseEntity.ok(foundStudent);
    }

    @GetMapping("/{studentId}/courses")
    public ResponseEntity<List<CourseDto>> getCourses(@PathVariable Long studentId){
        List<CourseDto> courses = service.findAllCourses(studentId);
        return courses.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(courses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id){
        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/courses")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id,
                                              @RequestParam Long courseId){
        enrollmentService.unenrollStudentFromCourse(id, courseId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id,
                                                 @RequestBody StudentDto studentDto) {
        Student updatedStudent = service.updateById(id, studentDto);
        return ResponseEntity.ok(updatedStudent);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Student> patchStudent(@PathVariable Long id,
                                                @RequestBody StudentDto studentDto) {
        Student updatedStudent = service.patchById(id, studentDto);
        return ResponseEntity.ok(updatedStudent);
    }
}
