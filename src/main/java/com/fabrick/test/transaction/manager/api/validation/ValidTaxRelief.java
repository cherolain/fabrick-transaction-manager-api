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
    // allows to specify a custom error message
    String message() default "{taxrelief.beneficiarytype.mismatch}";

    // allows to specify validation groups, useful for differentiating validation scenarios
    Class<?>[] groups() default {};

    // allows to attach additional metadata to the annotation, useful for custom processing
    Class<? extends Payload>[] payload() default {};
}