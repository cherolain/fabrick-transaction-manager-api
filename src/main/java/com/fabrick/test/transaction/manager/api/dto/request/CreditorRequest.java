package com.fabrick.test.transaction.manager.api.dto.request;

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
public class CreditorRequest {
    @Size(max = 70)
    @NotBlank
    private String name;

    @NotNull
    @Valid
    private CreditorAccountRequest account;

    @Valid
    private CreditorAddressRequest address;
}