package com.spring.JpaRelationships.mapper;

import com.spring.JpaRelationships.dto.CourseDto;
import com.spring.JpaRelationships.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toEntity(CourseDto dto);

    CourseDto toDto(Course course);
}
