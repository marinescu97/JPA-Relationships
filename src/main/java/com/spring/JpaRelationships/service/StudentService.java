package com.spring.JpaRelationships.service;

import com.spring.JpaRelationships.dto.CourseDto;
import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.exception.ResourceNotFoundException;
import com.spring.JpaRelationships.mapper.CourseMapper;
import com.spring.JpaRelationships.mapper.StudentMapper;
import com.spring.JpaRelationships.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StudentService {
    private final StudentRepository repository;
    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final String ERROR_MESSAGE = "Student not found with id ";

    public Student save(StudentDto dto){
        return repository.save(studentMapper.toEntity(dto));
    }

    public List<Student> findAll(){
        return repository.findAll();
    }

    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ERROR_MESSAGE + id));
    }

    public List<CourseDto> findAllCourses(Long studentId) {
        Student student = findById(studentId);

        return student.getCourses().stream()
                .map(courseMapper::toDto)
                .toList();
    }

    public void deleteById(Long id) {
        if (repository.existsById(id)){
            repository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(ERROR_MESSAGE + id);
        }
    }

    public Student updateById(Long id, StudentDto studentDto) {
        Student foundStudent = findById(id);
        studentMapper.updateStudentFromDto(studentDto, foundStudent);

        return repository.save(foundStudent);
    }

    public Student patchById(Long id, StudentDto studentDto) {
        Student foundStudent = findById(id);

        studentMapper.patchStudentFromDto(studentDto, foundStudent);

        return repository.save(foundStudent);
    }
}
