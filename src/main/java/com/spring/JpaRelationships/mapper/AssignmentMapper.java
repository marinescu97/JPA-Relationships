package com.spring.JpaRelationships.mapper;

import com.spring.JpaRelationships.dto.AssignmentDto;
import com.spring.JpaRelationships.entity.Assignment;
import com.spring.JpaRelationships.entity.Student;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    @Mapping(target = "student", source = "student")
    @Mapping(target = "id", ignore = true)
    Assignment toEntity(AssignmentDto dto, Student student);

    @Mapping(target = "id", ignore = true)
    void updateAssignmentFromDto(AssignmentDto dto, @MappingTarget Assignment assignment);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchAssignmentFromDto(AssignmentDto dto, @MappingTarget Assignment assignment);
}
