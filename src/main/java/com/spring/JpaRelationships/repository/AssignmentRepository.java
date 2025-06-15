package com.spring.JpaRelationships.repository;

import com.spring.JpaRelationships.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findAllByStudentId(Long studentId);
    Optional<Assignment> findByStudentIdAndId(Long studentId, Long id);
    void deleteByStudentId(Long studentId);
}
