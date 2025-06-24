package com.fabrick.test.transaction.manager.api.mapper;

import com.fabrick.test.transaction.manager.api.client.dto.response.balance.BalanceResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class BalanceMapperTest {

    private final BalanceMapper mapper = Mappers.getMapper(BalanceMapper.class);


    @Test
    void testNullBalanceIsMappedToZero() {
        // Arrange
        // Create a source object with null balance
        BalanceResponse source = BalanceResponse.builder()
                .balance(null)
                .build();

        // Act
        // Call the mapper method

        // Assert
        // Verify that the result is as expected (e.g., zero balance)
    }

}
