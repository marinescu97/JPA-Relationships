package com.spring.JpaRelationships.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class CourseDto implements Serializable {
    private String code;
    private String name;
}