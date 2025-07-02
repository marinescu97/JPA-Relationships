package com.spring.JpaRelationships.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AddressDto implements Serializable {
    private String street;
    private String zipCode;
    private String city;
}
