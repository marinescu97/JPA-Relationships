package com.spring.JpaRelationships.mapper;

import com.spring.JpaRelationships.dto.AddressDto;
import com.spring.JpaRelationships.entity.Address;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @MappingQualifiers.UpdateMapping
    @Mapping(target = "id", ignore = true)
    void updateAddressFromDto(AddressDto dto, @MappingTarget Address address);

    @MappingQualifiers.PatchMapping
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchAddressFromDto(AddressDto dto, @MappingTarget Address address);
}
