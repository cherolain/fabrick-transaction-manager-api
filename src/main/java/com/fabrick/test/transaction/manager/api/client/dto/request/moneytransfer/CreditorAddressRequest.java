package com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditorAddressRequest { // Da CreditorAddressRequestDto a CreditorAddressRequest
    @Size(max = 40, message = "address must not exceed 40 characters")
    private String address;

    private String city;

    @Pattern(regexp = "^[A-Z]{2}$", message = "countryCode must be ISO 3166-1 alpha-2")
    private String countryCode;
}