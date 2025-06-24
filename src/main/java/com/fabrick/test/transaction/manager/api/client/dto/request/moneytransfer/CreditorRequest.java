package com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer;

import com.fabrick.test.transaction.manager.api.validation.ValidCreditorAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidCreditorAddress
public class CreditorRequest {
    @Size(max = 70, message = "name must not exceed 70 characters")
    @NotBlank(message = "name must not be blank")
    private String name;

    @NotNull(message = "type must not be null")
    @Valid
    private CreditorAccountRequest account;

    @Valid
    private CreditorAddressRequest address;
}