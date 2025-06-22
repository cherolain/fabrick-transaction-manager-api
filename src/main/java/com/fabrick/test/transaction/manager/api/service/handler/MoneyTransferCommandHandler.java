package com.fabrick.test.transaction.manager.api.service.handler;

import com.fabrick.test.transaction.manager.api.client.GbsBankingClient;
import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.client.dto.response.moneytransfer.MoneyTransferResponse;
import com.fabrick.test.transaction.manager.api.service.template.RequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoneyTransferCommandHandler extends RequestHandler<MoneyTransferCommandHandler.Command, MoneyTransferResponse, MoneyTransferResponse> {

    /**
     * A nested record acting as a Data Transfer Object (DTO)
     * for this specific operation. Being declared 'public' and 'static' (implicitly for records),
     * it can be instantiated from outside (e.g., from the Facade).
     * Its purpose is to group all input parameters into a single type-safe object.
     */
    public record Command(
            String accountId,
            MoneyTransferRequest moneyTransferRequest,
            String timeZone
    ) {
    }

    private final GbsBankingClient client;

    /**
     * Executes the specific action for this command: calls the Feign client method
     * to create the money transfer, using the data encapsulated in the Command object.
     *
     * @param command The Command object, type-safe and complete.
     * @return The full response from the Fabrick API, including the envelope.
     */
    @Override
    protected GbsBankingResponse<MoneyTransferResponse> performAction(Command command) {
        return client.createMoneyTransfer(
                command.accountId(),
                command.moneyTransferRequest(),
                command.timeZone()
        );
    }

    /**
     * If needed, allows mapping the response to a different type by overriding this method.
     *
     * @param payload The payload extracted after status validation.
     * @return The final response formatted for the service client.
     */
    @Override
    protected MoneyTransferResponse mapToResponse(MoneyTransferResponse payload) {
        return payload;
    }
}