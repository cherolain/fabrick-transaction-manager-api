package com.fabrick.test.transaction.manager.api.service;

import com.fabrick.test.transaction.manager.api.client.FabrickFeignClient;
import com.fabrick.test.transaction.manager.api.client.dto.FabrickStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.balance.Balance;
import com.fabrick.test.transaction.manager.api.client.dto.response.FabrickApiResponse;
import com.fabrick.test.transaction.manager.api.utils.FabrickErrorCodeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private FabrickFeignClient fabrickClient;

    @Mock
    private FabrickErrorCodeMapper fabrickErrorCodeMapper;

    @InjectMocks
    private BalanceService balanceService;

    private final String ACCOUNT_ID = "12345";

    // --- Tests for getAccountBalance ---

    @Test
    @DisplayName("Should return balance when Fabrick status is OK")
    void getAccountBalance_shouldReturnBalance_whenFabrickStatusIsOk() {
        // Given: An accountId and a mock FabrickApiResponse with OK status
        Balance balance = new Balance(
                LocalDate.now(),
                BigDecimal.valueOf(1000.50),
                BigDecimal.valueOf(950.00),
                "EUR"
        );
        FabrickApiResponse<Balance> mockResponse = new FabrickApiResponse<>(FabrickStatus.OK, null, balance);

        // When: fabrickClient.getBalance is called and returns the mockResponse
        when(fabrickClient.retrieveAccountBalance(ACCOUNT_ID)).thenReturn(mockResponse);

        // Then: The method should return the correct Balance and interactions should be as expected
        Balance result = balanceService.getAccountBalance(ACCOUNT_ID);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000.50), result.getBalance());
        verify(fabrickClient, times(1)).retrieveAccountBalance(ACCOUNT_ID);
        verifyNoInteractions(fabrickErrorCodeMapper);
    }
}