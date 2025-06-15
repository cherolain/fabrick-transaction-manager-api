package com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaturalPersonBeneficiaryRequest { // Da NaturalPersonBeneficiaryRequestDto a NaturalPersonBeneficiaryRequest
    @NotBlank
    private String fiscalCode1;
    private String fiscalCode2;
    private String fiscalCode3;
    private String fiscalCode4;
    private String fiscalCode5;
}