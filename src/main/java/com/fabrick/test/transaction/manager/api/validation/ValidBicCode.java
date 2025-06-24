package com.fabrick.test.transaction.manager.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = BicCodeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBicCode {
    String message() default "{creditoraccount.bic.required.for.swift}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}