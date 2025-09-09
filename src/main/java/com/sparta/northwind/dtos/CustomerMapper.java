package com.sparta.northwind.dtos;

import com.sparta.northwind.entities.Customer;
import org.mapstruct.Mapper;

/**
 * Mapper for Customer entity and CustomerDto.
 * This mapper is responsible for converting between Customer entities and CustomerDto objects.
 * It does this by mapping the properties of the entity to the corresponding properties of the DTO.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDto toDto(Customer customer);

    Customer toEntity(CustomerDto customerDto);
}
