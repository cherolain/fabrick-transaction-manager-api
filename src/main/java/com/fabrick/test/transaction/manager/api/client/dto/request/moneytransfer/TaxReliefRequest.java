package com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer;

import com.fabrick.test.transaction.manager.api.validation.ValidTaxRelief;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidTaxRelief
public class TaxReliefRequest {
    @Pattern(regexp = "119R|DL50|L296|L449|L234")
    private String taxReliefId;

    @NotNull(message = "isCondoUpgrade must not be null")
    private Boolean isCondoUpgrade;

    @NotBlank(message = "creditorFiscalCode must not be blank")
    private String creditorFiscalCode;

    @NotNull(message = "beneficiaryType must not be null")
    @Pattern(regexp = "NATURAL_PERSON|LEGAL_PERSON",  message = "beneficiaryType must be NATURAL_PERSON or LEGAL_PERSON")
    private String beneficiaryType;

    @Valid
    private NaturalPersonBeneficiaryRequest naturalPersonBeneficiary;

    @Valid
    private LegalPersonBeneficiaryRequest legalPersonBeneficiary;
}