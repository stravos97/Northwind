package com.sparta.northwind.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link com.sparta.northwind.entities.Customer}
 */
public class CustomerDto implements Serializable {
    @Size(max = 5)
    @NotBlank(message = "Customer ID cannot be null or empty")
    private final String customerID;
    @NotNull
    @Size(max = 40)
    private final String companyName;
    @Size(max = 30)
    private final String contactName;
    @Size(max = 15)
    private final String city;

    public CustomerDto(String customerID, String companyName, String contactName, String city) {
        this.customerID = customerID;
        this.companyName = companyName;
        this.contactName = contactName;
        this.city = city;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDto entity = (CustomerDto) o;
        return Objects.equals(this.customerID, entity.customerID) &&
                Objects.equals(this.companyName, entity.companyName) &&
                Objects.equals(this.contactName, entity.contactName) &&
                Objects.equals(this.city, entity.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerID, companyName, contactName, city);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "customerID = " + customerID + ", " +
                "companyName = " + companyName + ", " +
                "contactName = " + contactName + ", " +
                "city = " + city + ")";
    }


}