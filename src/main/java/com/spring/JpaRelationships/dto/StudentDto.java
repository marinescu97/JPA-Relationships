package com.spring.JpaRelationships.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class StudentDto implements Serializable {
    private String name;
    private String email;
    private AddressDto address;
}
