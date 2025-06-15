package com.fabrick.test.transaction.manager.api.validation;

import com.fabrick.test.transaction.manager.api.dto.request.TaxReliefRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaxReliefValidator implements ConstraintValidator<ValidTaxRelief, TaxReliefRequest> {

    private static final String NATURAL_PERSON = "NATURAL_PERSON";
    private static final String LEGAL_PERSON = "LEGAL_PERSON";

    private static final String MSG_NATURAL_REQUIRED = "{taxrelief.naturalpersonbeneficiary.required}";
    private static final String MSG_NATURAL_NOT_ALLOWED = "{taxrelief.naturalpersonbeneficiary.notallowed}";
    private static final String MSG_LEGAL_REQUIRED = "{taxrelief.legalpersonbeneficiary.required}";
    private static final String MSG_LEGAL_NOT_ALLOWED = "{taxrelief.legalpersonbeneficiary.notallowed}";

    @Override
    public void initialize(ValidTaxRelief constraintAnnotation) {}

    @Override
    public boolean isValid(TaxReliefRequest taxRelief, ConstraintValidatorContext context) {
        if (taxRelief == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        String beneficiaryType = taxRelief.getBeneficiaryType();

        if (NATURAL_PERSON.equals(beneficiaryType)) {
            if (taxRelief.getNaturalPersonBeneficiary() == null) {
                context.buildConstraintViolationWithTemplate(MSG_NATURAL_REQUIRED)
                        .addPropertyNode("naturalPersonBeneficiary")
                        .addConstraintViolation();
                isValid = false;
            }
            if (taxRelief.getLegalPersonBeneficiary() != null) {
                context.buildConstraintViolationWithTemplate(MSG_LEGAL_NOT_ALLOWED)
                        .addPropertyNode("legalPersonBeneficiary")
                        .addConstraintViolation();
                isValid = false;
            }
        } else if (LEGAL_PERSON.equals(beneficiaryType)) {
            if (taxRelief.getLegalPersonBeneficiary() == null) {
                context.buildConstraintViolationWithTemplate(MSG_LEGAL_REQUIRED)
                        .addPropertyNode("legalPersonBeneficiary")
                        .addConstraintViolation();
                isValid = false;
            }
            if (taxRelief.getNaturalPersonBeneficiary() != null) {
                context.buildConstraintViolationWithTemplate(MSG_NATURAL_NOT_ALLOWED)
                        .addPropertyNode("naturalPersonBeneficiary")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}