package com.fabrick.test.transaction.manager.api.validation.model; // Suggerisco un nuovo sub-package

import lombok.Getter;
import java.util.Set;

/**
 * An enum representing the geographical and regulatory type of a bank account,
 * derived from its account code (typically an IBAN).
 * This centralizes the logic for determining if an account is Italian, SEPA, or Extra-SEPA.
 */
@Getter
public enum AccountType {
    ITALIAN,
    SEPA_NOT_ITALIAN,
    EXTRA_SEPA,
    UNKNOWN;

    private static final Set<String> SEPA_COUNTRIES = Set.of(
            "AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GR", "HR",
            "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MT", "NL", "NO", "PL",
            "PT", "RO", "SE", "SI", "SK", "CH", "GB", "SM", "MC", "VA", "AD", "GI"
    );

    /**
     * Factory method to determine the AccountType from an account code string.
     * @param accountCode The account code, typically an IBAN.
     * @return The determined AccountType.
     */
    public static AccountType fromAccountCode(String accountCode) {
        if (accountCode == null || accountCode.length() < 2) {
            return UNKNOWN;
        }

        String countryCode = accountCode.substring(0, 2);
        if (!countryCode.matches("[A-Z]{2}")) {
            return UNKNOWN; // Invalid country code format without two uppercase letters
        }

        if ("IT".equals(countryCode)) {
            return ITALIAN;
        }

        if (SEPA_COUNTRIES.contains(countryCode)) {
            return SEPA_NOT_ITALIAN;
        }

        return EXTRA_SEPA;
    }

    /**
     * Helper method to check if the account belongs to the SEPA area (including Italy).
     * @return true if the account is ITALIAN or SEPA_NOT_ITALIAN.
     */
    public boolean isSepa() {
        return this == ITALIAN || this == SEPA_NOT_ITALIAN;
    }
}