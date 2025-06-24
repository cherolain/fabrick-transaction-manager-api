package com.fabrick.test.transaction.manager.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation to be applied on a MoneyTransferRequest.
 * It ensures that 'executionDate' is present if 'isInstant' is false.
 */
@Constraint(validatedBy = ExecutionDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidExecutionDate {
    String message() default "{moneytransfer.executiondate.required.for.noninstant}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}