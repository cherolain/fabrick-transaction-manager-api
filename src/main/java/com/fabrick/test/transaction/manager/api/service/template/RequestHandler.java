package com.fabrick.test.transaction.manager.api.service.template;

import com.fabrick.test.transaction.manager.api.client.dto.GbsBankingStatus;
import com.fabrick.test.transaction.manager.api.client.dto.response.GbsBankingResponse;
import com.fabrick.test.transaction.manager.api.exception.ErrorCode;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingApiException;
import com.fabrick.test.transaction.manager.api.exception.GbsBankingBusinessException;
import com.fabrick.test.transaction.manager.api.exception.InternalApplicationException;
import com.fabrick.test.transaction.manager.api.utils.GbsBankingErrorCodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Slf4j
public abstract class RequestHandler<TRequest, TApiPayload, TResponse> {

    /**
     * Template method that manages the entire request/response flow.
     * Executes the specific action, handles the standard API response, and maps the result.
     *
     * @param request The request object.
     * @return The final response for the service client.
     */
    public final TResponse handle(TRequest request) {
        // use the specific class name as operation description
        String operationDescription = this.getClass().getSimpleName();
        log.info("Starting operation [{}], request: {}", operationDescription, request);

        try {
            // Step 1: execute specific action of the subclass (e.g., Feign client call).
            GbsBankingResponse<TApiPayload> apiResponse = performAction(request);

            // Step 2: Handle the standard API response (common OK/KO logic).
            TApiPayload payload = handleApiResponse(apiResponse, operationDescription);

            // Step 3: Map the raw payload to the final response (delegated logic).
            TResponse finalResponse = mapToResponse(payload);

            log.info("Operation [{}] completed successfully.", operationDescription);
            return finalResponse;

        } catch (GbsBankingBusinessException | GbsBankingApiException e) {
            log.error("API error during operation [{}]: {}", operationDescription, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during operation [{}]: {}", operationDescription, e.getMessage(), e);
            throw new InternalApplicationException("Unexpected internal error during: " + operationDescription, e, ErrorCode.UNEXPECTED_ERROR);
        }
    }


    /**
     * Private method that centralizes the analysis of the GbsBanking response envelope.
     * Checks the status and payload, handling edge cases.
     *
     * @param response The response envelope from the API.
     * @param operationDescription The description of the current operation.
     * @return The extracted payload if status is OK.
     * @throws GbsBankingBusinessException if the status is KO.
     * @throws InternalApplicationException for unexpected or unhandled statuses.
     */
    private TApiPayload handleApiResponse(GbsBankingResponse<TApiPayload> response, String operationDescription) {
        if (GbsBankingStatus.OK.equals(response.getStatus())) {
            return Optional.ofNullable(response.getPayload())
                    .orElseThrow(() -> new InternalApplicationException(
                            "API returned OK status but a null payload for operation: " + operationDescription,
                            ErrorCode.UNEXPECTED_ERROR
                    ));
        }

        if (GbsBankingStatus.KO.equals(response.getStatus())) {
            // business error with HTTP 200
            log.error("API returned KO status for operation: {}. Errors: {}", operationDescription, response.getErrors());
            throw new GbsBankingBusinessException(response.getErrors(),
                    response.getErrors().stream()
                            .map(error -> GbsBankingErrorCodeMapper.resolveInternalErrorCode(error, HttpStatus.OK))
                            .toList());
        }

        throw new InternalApplicationException("Unhandled status received from API for " + operationDescription, ErrorCode.UNEXPECTED_ERROR);
    }

    /**
     * Executes the concrete action, typically a call to the Feign client.
     * Each subclass will implement this method for its specific operation.
     *
     * @param request The request object.
     * @return The complete response from the external API, including the envelope.
     */
    protected abstract GbsBankingResponse<TApiPayload> performAction(TRequest request);

    /**
     * Transforms the payload extracted from the API into the final response for our service.
     * Allows decoupling of the external data model from the internal one.
     *
     * @param payload The payload extracted after status validation.
     * @return The final response formatted for the service client.
     */
    protected abstract TResponse mapToResponse(TApiPayload payload);
}