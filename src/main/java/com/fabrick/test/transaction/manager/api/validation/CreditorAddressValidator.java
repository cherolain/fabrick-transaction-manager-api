package com.fabrick.test.transaction.manager.api.validation;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.CreditorRequest;
import com.fabrick.test.transaction.manager.api.validation.model.AccountType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreditorAddressValidator implements ConstraintValidator<ValidCreditorAddress, CreditorRequest> {

    private static final String MSG_ADDRESS_REQUIRED = "{creditor.address.required.for.international}";

    @Override
    public boolean isValid(CreditorRequest creditor, ConstraintValidatorContext context) {
        if (creditor == null || creditor.getAccount() == null) {
            return true;
        }

        AccountType accountType = AccountType.fromAccountCode(creditor.getAccount().getAccountCode());

        // l'indirizzo è richiesto per tutti i conti NON ITALIANI.
        if (accountType != AccountType.ITALIAN && accountType != AccountType.UNKNOWN) {
            if (creditor.getAddress() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(MSG_ADDRESS_REQUIRED)
                        .addPropertyNode("address")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}