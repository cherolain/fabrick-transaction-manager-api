package com.fabrick.test.transaction.manager.api.dto.request;

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

    @NotNull
    private Boolean isCondoUpgrade;

    @NotBlank
    private String creditorFiscalCode;

    @NotNull
    @Pattern(regexp = "NATURAL_PERSON|LEGAL_PERSON",  message = "beneficiaryType must be NATURAL_PERSON or LEGAL_PERSON")
    private String beneficiaryType;

    @Valid
    private NaturalPersonBeneficiaryRequest naturalPersonBeneficiary;

    @Valid
    private LegalPersonBeneficiaryRequest legalPersonBeneficiary;
}