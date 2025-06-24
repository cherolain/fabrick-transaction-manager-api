package com.fabrick.test.transaction.manager.api.mapper;

import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionListResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.transactions.TransactionTypeResponse;
import com.fabrick.test.transaction.manager.api.dto.transactions.TransactionApiResponse;
import com.fabrick.test.transaction.manager.api.dto.transactions.TransactionListApiResponse;
import com.fabrick.test.transaction.manager.api.dto.transactions.TransactionTypeApiResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
)
public interface TransactionMapper {

    TransactionListApiResponse toTransactionApiResponse(TransactionListResponse gbsTransactionListResponse);

    TransactionApiResponse toTransactionApiResponse(TransactionResponse gbsTransactionResponse);

    TransactionTypeApiResponse toTransactionTypeApiResponse(TransactionTypeResponse gbsTransactionTypeResponse);
}
