package com.spring.JpaRelationships.service;

import com.spring.JpaRelationships.dto.CourseDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Course;
import com.spring.JpaRelationships.exception.ResourceNotFoundException;
import com.spring.JpaRelationships.mapper.CourseMapper;
import com.spring.JpaRelationships.mapper.StudentMapper;
import com.spring.JpaRelationships.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class CourseService {
    private final CourseRepository repository;
    private final CourseMapper courseMapper;
    private final StudentMapper studentMapper;

    public Course findById(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    public List<CourseDto> findAll() {
        return repository.findAll().stream()
                .map(courseMapper::toDto)
                .toList();
    }

    public List<StudentDto> findAllStudents(Long courseId) {
        Course course = findById(courseId);

        return course.getStudents().stream()
                .map(studentMapper::toDto)
                .toList();
    }
}
