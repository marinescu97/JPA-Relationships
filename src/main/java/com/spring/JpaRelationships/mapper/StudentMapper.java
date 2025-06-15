package com.spring.JpaRelationships.mapper;

import com.spring.JpaRelationships.dto.StudentDto;
import com.spring.JpaRelationships.entity.Student;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", qualifiedBy = MappingQualifiers.UpdateMapping.class)
    void updateStudentFromDto(StudentDto dto, @MappingTarget Student student);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "address", qualifiedBy = MappingQualifiers.PatchMapping.class)
    void patchStudentFromDto(StudentDto dto, @MappingTarget Student student);

    StudentDto toDto(Student student);

    Student toEntity(StudentDto dto);
}
