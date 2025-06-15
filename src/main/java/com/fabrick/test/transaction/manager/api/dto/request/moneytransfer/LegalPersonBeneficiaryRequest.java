package com.fabrick.test.transaction.manager.api.dto.request.moneytransfer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalPersonBeneficiaryRequest { // Da LegalPersonBeneficiaryRequestDto a LegalPersonBeneficiaryRequest
    @NotBlank
    private String fiscalCode;

    private String legalRepresentativeFiscalCode;
}