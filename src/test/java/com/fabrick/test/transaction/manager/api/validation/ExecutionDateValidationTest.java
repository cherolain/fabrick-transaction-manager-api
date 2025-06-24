package com.fabrick.test.transaction.manager.api.validation;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.MoneyTransferRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecutionDateValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest(name = "Scenario {index}: isInstant={0}, date={1} -> violations={2}")
    @MethodSource("executionDateScenarios")
    void testExecutionDateLogic(Boolean isInstant, LocalDate executionDate, int expectedViolations) {
        // ARRANGE
        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setIsInstant(isInstant);
        request.setExecutionDate(executionDate);

        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        // ASSERT
        long ourRuleViolations = violations.stream()
                .filter(v -> v.getConstraintDescriptor().getAnnotation() instanceof ValidExecutionDate)
                .count();

        assertEquals(expectedViolations, ourRuleViolations);
    }

    private static Stream<Arguments> executionDateScenarios() {
        return Stream.of(
                //                          isInstant,  executionDate,      expectedViolations
                Arguments.of(true,       null,               0),  // Istantaneo, data non necessaria -> VALIDO
                Arguments.of(true,       LocalDate.now(),    0),  // Istantaneo, data presente -> VALIDO
                Arguments.of(false,      LocalDate.now(),    0),  // Non istantaneo, data presente -> VALIDO
                Arguments.of(null,       LocalDate.now(),    0),  // isInstant nullo (default false), data presente -> VALIDO
                Arguments.of(false,      null,               1),  // Non istantaneo, data assente -> NON VALIDO
                Arguments.of(null,       null,               1)   // isInstant nullo, data assente -> NON VALIDO
        );
    }
}