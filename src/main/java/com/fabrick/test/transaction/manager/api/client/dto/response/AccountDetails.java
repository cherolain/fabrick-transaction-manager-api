package com.fabrick.test.transaction.manager.api.client.dto.response; // Scegli il package appropriato per le DTO di risposta

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetails {

    /**
     * The ID of the account.
     */
    private String accountId;

    /**
     * The IBAN code of the account.
     */
    private String iban;

    /**
     * The alias of the account (if defined).
     */
    private String accountAlias;

    /**
     * The account product name.
     */
    private String productName;

    /**
     * The full name (or names) of the account holder (or holders).
     */
    private String accountHolderName;

    /**
     * The native currency of the account.
     */
    private String currency;
}