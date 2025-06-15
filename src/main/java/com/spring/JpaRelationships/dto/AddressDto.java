package com.spring.JpaRelationships.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class AddressDto implements Serializable {
    private String street;
    private String zipCode;
    private String city;
}
