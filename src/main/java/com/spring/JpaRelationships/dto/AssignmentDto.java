package com.spring.JpaRelationships.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AssignmentDto implements Serializable {
    private String title;
    private LocalDate dueDate;
}
