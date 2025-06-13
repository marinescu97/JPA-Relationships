package com.spring.JpaRelationships.service;

import com.spring.JpaRelationships.dto.AssignmentDto;
import com.spring.JpaRelationships.entity.Assignment;
import com.spring.JpaRelationships.entity.Student;
import com.spring.JpaRelationships.exception.ResourceNotFoundException;
import com.spring.JpaRelationships.mapper.AssignmentMapper;
import com.spring.JpaRelationships.repository.AssignmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class AssignmentService {
    private final AssignmentRepository repository;
    private final StudentService studentService;
    private final AssignmentMapper mapper;

    private final String ERROR_MESSAGE = "Assignment not found with id ";


    public List<Assignment> findAll() {
        return repository.findAll();
    }

    public List<Assignment> findAllByStudentId(Long id){
        return repository.findAllByStudentId(id);
    }

    public List<Assignment> saveAll(Long studentId, List<AssignmentDto> dtoList) {
        Student foundStudent = studentService.findById(studentId);

        Iterable<Assignment> assignments = dtoList.stream()
                .map(dto -> mapper.toEntity(dto, foundStudent))
                .toList();

        return repository.saveAll(assignments);
    }

    public void deleteById(Long id){
        if (repository.existsById(id)){
            repository.deleteById(id);
        } else {
            throw new ResourceNotFoundException(ERROR_MESSAGE + id);
        }
    }

    @Transactional
    public void deleteAllByStudentId(Long studentId) {
        studentService.findById(studentId);
        repository.deleteByStudentId(studentId);
    }

    public Assignment updateAssignment(Long assignmentId, AssignmentDto dto) {
        Assignment foundAssignment = findById(assignmentId);
        mapper.updateAssignmentFromDto(dto, foundAssignment);

        return foundAssignment;
    }

    public Assignment patchAssignment(Long id, AssignmentDto dto){
        Assignment foundAssignment = findById(id);
        mapper.patchAssignmentFromDto(dto, foundAssignment);

        return foundAssignment;
    }

    private Assignment findById(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ERROR_MESSAGE + id));
    }
}
