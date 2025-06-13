package com.spring.JpaRelationships.controller;

import com.spring.JpaRelationships.dto.AssignmentDto;
import com.spring.JpaRelationships.entity.Assignment;
import com.spring.JpaRelationships.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@RequestMapping("/api")
public class AssignmentController {
    private final AssignmentService service;

    private final String STUDENT_PATH = "/students/{studentId}";
    private final String ASSIGNMENT_PATH = "/assignments/{assignmentId}";

    @GetMapping("/assignments")
    public ResponseEntity<List<Assignment>> getAllAssignments(){
        List<Assignment> assignments = service.findAll();

        return assignments.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(assignments);
    }

    @GetMapping(STUDENT_PATH + "/assignments")
    public ResponseEntity<List<Assignment>> getAllByStudentId(@PathVariable Long studentId){
        List<Assignment> assignments = service.findAllByStudentId(studentId);
        return assignments.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(assignments);
    }

    @PostMapping(STUDENT_PATH + "/assignments")
    public ResponseEntity<List<Assignment>> createAll(@PathVariable Long studentId, @RequestBody List<AssignmentDto> dtoList){
        List<Assignment> createdAssignments = service.saveAll(studentId, dtoList);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignments);
    }

    @DeleteMapping(ASSIGNMENT_PATH)
    public ResponseEntity<Void> deleteById(@PathVariable Long assignmentId){
        service.deleteById(assignmentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(STUDENT_PATH + "/assignments")
    public ResponseEntity<Void> deleteAllByStudentId(@PathVariable Long studentId){
        service.deleteAllByStudentId(studentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(ASSIGNMENT_PATH)
    public ResponseEntity<Assignment> updateAssignment(@PathVariable Long assignmentId,
                                                       @RequestBody AssignmentDto dto){
        Assignment updatedAssignment = service.updateAssignment(assignmentId, dto);
        return ResponseEntity.ok(updatedAssignment);
    }

    @PatchMapping(ASSIGNMENT_PATH)
    public ResponseEntity<Assignment> patchAssignment(@PathVariable Long assignmentId,
                                                      @RequestBody AssignmentDto dto){
        Assignment patchedAssignment = service.patchAssignment(assignmentId, dto);
        return ResponseEntity.ok(patchedAssignment);
    }
}
