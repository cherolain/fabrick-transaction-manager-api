package com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditorAddressResponse {
    private String address;
    private String city;
    private String countryCode;
}