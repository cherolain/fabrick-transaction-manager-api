package com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegalPersonBeneficiaryRequest { // Da LegalPersonBeneficiaryRequestDto a LegalPersonBeneficiaryRequest
    @NotBlank(message = "fiscalCode must not be blank")
    private String fiscalCode;

    private String legalRepresentativeFiscalCode;
}