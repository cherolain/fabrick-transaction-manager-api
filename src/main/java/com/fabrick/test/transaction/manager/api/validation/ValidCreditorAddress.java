package com.fabrick.test.transaction.manager.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CreditorAddressValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCreditorAddress {
    String message() default "Creditor address is mandatory for this destination";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}