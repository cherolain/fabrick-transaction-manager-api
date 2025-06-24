package com.fabrick.test.transaction.manager.api.mapper;

import com.fabrick.test.transaction.manager.api.client.dto.response.balance.BalanceResponse;
import com.fabrick.test.transaction.manager.api.dto.balance.BalanceApiResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BalanceMapperTest {

    private final BalanceMapper mapper = Mappers.getMapper(BalanceMapper.class);

    @Test
    void shouldMapBalanceCorrectly() {
        BalanceResponse source = BalanceResponse.builder()
                .balance(new BigDecimal("123.45"))
                .currency("EUR")
                .availableBalance(new BigDecimal("100.00"))
                .build();

        BalanceApiResponse result = mapper.toBalanceApiResponse(source);

        assertAll(
                () -> assertEquals(new BigDecimal("123.45"), result.getBalance()),
                () -> assertEquals("EUR", result.getCurrency()),
                () -> assertEquals(new BigDecimal("100.00"), result.getAvailableBalance())
        );
    }

    @Test
    void shouldMapNullBalanceToNull() {
        BalanceResponse source = BalanceResponse.builder()
                .balance(null)
                .currency("EUR")
                .availableBalance(null)
                .build();

        BalanceApiResponse result = mapper.toBalanceApiResponse(source);

        assertAll(
                () -> assertNull(result.getBalance()),
                () -> assertEquals("EUR", result.getCurrency()),
                () -> assertNull(result.getAvailableBalance())
        );
    }

}
