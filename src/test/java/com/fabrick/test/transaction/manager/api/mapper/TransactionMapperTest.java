package com.fabrick.test.transaction.manager.api.mapper;

import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionTypeResponse;
import com.fabrick.test.transaction.manager.api.dto.transactions.TransactionApiResponse;
import com.fabrick.test.transaction.manager.api.dto.transactions.TransactionListApiResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionMapperTest {

    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void testNullListIsMappedToEmptyList() {
        // Arrange
        TransactionListResponse source = new TransactionListResponse();
        source.setList(null);

        // Act
        TransactionListApiResponse result = mapper.toTransactionApiResponse(source);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getList());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    void shouldCorrectlyMapTransactionList() {
        var transactionTypeSource = new TransactionTypeResponse("GBS_TRANSACTION_TYPE", "ACCREDITO");
        var transactionSource = new TransactionResponse();
        transactionSource.setAccountingDate(LocalDate.of(2024, 5, 21));
        transactionSource.setValueDate(LocalDate.of(2024, 5, 22));
        transactionSource.setAmount(new BigDecimal("1234.56"));
        transactionSource.setCurrency("EUR");
        transactionSource.setDescription("Stipendio");
        transactionSource.setType(transactionTypeSource);
        transactionSource.setTransactionId("TXN123");

        var sourceList = new TransactionListResponse();
        sourceList.setList(List.of(transactionSource));

        TransactionListApiResponse result = mapper.toTransactionApiResponse(sourceList);

        assertNotNull(result);
        assertNotNull(result.getList());
        assertEquals(1, result.getList().size(), "La lista di destinazione dovrebbe avere un elemento.");

        TransactionApiResponse resultTransaction = result.getList().getFirst();
        assertNotNull(resultTransaction);
        assertEquals(transactionSource.getAccountingDate(), resultTransaction.getAccountingDate());
        assertEquals(transactionSource.getValueDate(), resultTransaction.getValueDate());
        assertEquals(0, transactionSource.getAmount().compareTo(resultTransaction.getAmount()), "Gli importi dovrebbero essere identici.");
        assertEquals(transactionSource.getCurrency(), resultTransaction.getCurrency());
        assertEquals(transactionSource.getDescription(), resultTransaction.getDescription());

        assertNotNull(resultTransaction.getType());
        assertEquals(transactionTypeSource.getEnumeration(), resultTransaction.getType().getEnumeration());
        assertEquals(transactionTypeSource.getValue(), resultTransaction.getType().getValue());
    }

    @Test
    void shouldMapEmptySourceToEmptyList() {
        var source = new TransactionListResponse();
        source.setList(List.of());

        // Act
        var result = mapper.toTransactionApiResponse(source);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getList());
        assertTrue(result.getList().isEmpty());
    }
}