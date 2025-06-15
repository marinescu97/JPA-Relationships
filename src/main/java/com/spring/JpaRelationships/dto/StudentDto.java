package com.spring.JpaRelationships.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class StudentDto implements Serializable {
    private String name;
    private String email;
    private AddressDto address;
}
