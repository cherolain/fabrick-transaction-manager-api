package com.fabrick.test.transaction.manager.api.validation;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.CreditorAccountRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class BicCodeValidator implements ConstraintValidator<ValidBicCode, CreditorAccountRequest> {

    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}\\d{2}.*");

    @Override
    public boolean isValid(CreditorAccountRequest creditorAccount, ConstraintValidatorContext context) {
        if (creditorAccount == null || creditorAccount.getAccountCode() == null) {
            return true;
        }

        String accountCode = creditorAccount.getAccountCode();
        String bicCode = creditorAccount.getBicCode();

        // L'ipotesi è che i conti non-IBAN non inizino con due lettere.
        if (!IBAN_PATTERN.matcher(accountCode).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("bicCode")
                    .addConstraintViolation();
            return bicCode != null && !bicCode.isBlank();
        }

        return true;
    }
}