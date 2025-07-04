package com.fabrick.test.transaction.manager.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for TaxReliefRequest.
 * Ensures consistency between 'beneficiaryType' and the presence/absence
 * of 'naturalPersonBeneficiary' and 'legalPersonBeneficiary' fields.
 * This annotation is applied at the class level of TaxReliefRequest.
 */
@Constraint(validatedBy = TaxReliefValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTaxRelief {
    String message() default "{taxrelief.beneficiarytype.mismatch}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}