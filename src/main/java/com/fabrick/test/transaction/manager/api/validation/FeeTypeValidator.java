package com.fabrick.test.transaction.manager.api.validation;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.validation.model.AccountType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FeeTypeValidator implements ConstraintValidator<ValidFeeType, MoneyTransferRequest> {

    private static final String FEE_TYPE_OUR = "OUR";
    private static final String FEE_TYPE_BEN = "BEN";
    private static final String MSG_FEE_TYPE_NOT_ALLOWED = "{feetype.not.allowed.for.sepa}";

    @Override
    public boolean isValid(MoneyTransferRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getFeeType() == null || request.getCreditor() == null || request.getCreditor().getAccount() == null) {
            return true;
        }

        AccountType accountType = AccountType.fromAccountCode(request.getCreditor().getAccount().getAccountCode());
        String feeType = request.getFeeType();

        // La regola: OUR e BEN sono validi solo se il conto NON è SEPA.
        if ((FEE_TYPE_OUR.equals(feeType) || FEE_TYPE_BEN.equals(feeType)) && accountType.isSepa()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MSG_FEE_TYPE_NOT_ALLOWED)
                    .addPropertyNode("feeType")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}