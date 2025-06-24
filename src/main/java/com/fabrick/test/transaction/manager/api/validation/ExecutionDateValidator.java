package com.fabrick.test.transaction.manager.api.validation;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExecutionDateValidator implements ConstraintValidator<ValidExecutionDate, MoneyTransferRequest> {

    @Override
    public boolean isValid(MoneyTransferRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        Boolean isInstant = request.getIsInstant();

        boolean isInstantTransfer = isInstant != null && isInstant;

        return isInstantTransfer || request.getExecutionDate() != null;
    }
}