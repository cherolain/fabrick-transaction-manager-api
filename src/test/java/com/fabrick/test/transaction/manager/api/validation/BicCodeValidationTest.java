package com.fabrick.test.transaction.manager.api.validation;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.CreditorAccountRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BicCodeValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @ParameterizedTest(name = "{index}: accountCode={0}, bicCode={1} -> {3}")
    @MethodSource("bicCodeScenarios")
    void testBicCodeLogic(String accountCode, String bicCode, int expectedViolations, String scenarioDescription, String expectedPropertyPath) {
        var accountRequest = new CreditorAccountRequest(accountCode, bicCode);
        Set<ConstraintViolation<CreditorAccountRequest>> violations = validator.validate(accountRequest);
        assertEquals(expectedViolations, violations.size(), "Scenario: " + scenarioDescription);

        if (expectedViolations > 0) {
            String propertyPath = violations.iterator().next().getPropertyPath().toString();
            assertEquals(expectedPropertyPath, propertyPath, "Scenario: " + scenarioDescription);
        }
    }

    private static Stream<Arguments> bicCodeScenarios() {
        return Stream.of(
                Arguments.of("IT29NWBK60161331926819", "BICCODE1", 0, "IBAN with BIC -> Valid", ""),
                Arguments.of("IT29NWBK60161331926819", null,       0, "IBAN without BIC -> Valid", ""),
                Arguments.of("123456789",              "BICCODE1", 0, "Non-IBAN with BIC -> Valid", ""),
                Arguments.of("123456789",              null,       1, "Non-IBAN without BIC -> Invalid", "bicCode"),
                Arguments.of("123456789",              " ",        1, "Non-IBAN with blank BIC -> Invalid", "bicCode")
        );
    }
}