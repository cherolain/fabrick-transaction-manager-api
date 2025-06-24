package com.fabrick.test.transaction.manager.api.validation;

import com.fabrick.test.transaction.manager.api.client.dto.request.moneytransfer.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTransferRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private MoneyTransferRequest buildValidRequest() {
        MoneyTransferRequest request = new MoneyTransferRequest();
        request.setDescription("Valid description");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("EUR");
        request.setExecutionDate(LocalDate.now().plusDays(1));
        request.setIsUrgent(false);
        request.setIsInstant(false);
        request.setFeeType("SHA");
        CreditorRequest creditor = new CreditorRequest();
        creditor.setName("John Doe");
        creditor.setAccount(new CreditorAccountRequest("IT60X0542811101000000123456", null));
        request.setCreditor(creditor);
        return request;
    }

    @Test
    @DisplayName("Valid request should have no violations")
    void validRequest_shouldHaveNoViolations() {
        MoneyTransferRequest request = buildValidRequest();
        // Aggiungiamo un taxRelief valido per testare il caso ideale
        TaxReliefRequest taxRelief = new TaxReliefRequest();
        taxRelief.setIsCondoUpgrade(false);
        taxRelief.setCreditorFiscalCode("ABCDEF12G34H567I");
        taxRelief.setBeneficiaryType("NATURAL_PERSON");
        taxRelief.setNaturalPersonBeneficiary(new NaturalPersonBeneficiaryRequest("NPFC12345", null, null, null, null));
        taxRelief.setTaxReliefId("DL50");
        request.setTaxRelief(taxRelief);

        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Expected no validation violations for a valid request.");
    }

    @Test
    @DisplayName("Description too long should return violation")
    void descriptionTooLong_shouldReturnViolation() {
        MoneyTransferRequest request = buildValidRequest();
        request.setDescription("a".repeat(141));
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected violation for description too long.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")),
                "Expected violation on 'description' field.");
    }

    @Test
    @DisplayName("Null amount should return violation")
    void nullAmount_shouldReturnViolation() {
        MoneyTransferRequest request = buildValidRequest();
        request.setAmount(null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected violation for null amount.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")),
                "Expected violation on 'amount' field.");
    }

    @Test
    @DisplayName("Blank creditor name should return violation")
    void blankCreditorName_shouldReturnViolation() {
        MoneyTransferRequest request = buildValidRequest();
        request.getCreditor().setName("");
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected violation for blank creditor name.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("creditor.name")),
                "Expected violation on 'creditor.name' field.");
    }

    @Test
    @DisplayName("Null creditor account should return violation")
    void nullCreditorAccount_shouldReturnViolation() {
        MoneyTransferRequest request = buildValidRequest();
        request.getCreditor().setAccount(null);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected violation for null creditor account.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("creditor.account")),
                "Expected violation on 'creditor.account' field.");
    }

    @Test
    @DisplayName("Execution date in past should return violation")
    void executionDateInPast_shouldReturnViolation() {
        MoneyTransferRequest request = buildValidRequest();
        request.setExecutionDate(LocalDate.now().minusDays(1));
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected violation for execution date in past.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("executionDate")),
                "Expected violation on 'executionDate' field.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "EU", "EURO", "1234"})
    @DisplayName("Invalid currency should return violation")
    void invalidCurrency_shouldReturnViolation(String currency) {
        MoneyTransferRequest request = buildValidRequest();
        request.setCurrency(currency);
        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Expected violation for invalid currency.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currency")),
                "Expected violation on 'currency' field.");
    }

    @Test
    @DisplayName("Tax relief beneficiary type mismatch should return multiple violations for conditional fields")
    void taxReliefBeneficiaryTypeMismatch_shouldReturnViolationsForConditionalFields() {
        MoneyTransferRequest request = buildValidRequest();
        TaxReliefRequest taxRelief = new TaxReliefRequest();
        taxRelief.setIsCondoUpgrade(true);
        taxRelief.setCreditorFiscalCode("ABCDEF12G34H567I");
        taxRelief.setBeneficiaryType("NATURAL_PERSON"); // Settiamo NATURAL_PERSON
        taxRelief.setTaxReliefId("DL50"); // Campo obbligatorio per TaxReliefRequest
        taxRelief.setLegalPersonBeneficiary(new LegalPersonBeneficiaryRequest("12345678901", "ABCDEF12G34H567J")); // Ma impostiamo legalPersonBeneficiary
        request.setTaxRelief(taxRelief);

        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Expected violations for tax relief beneficiary type mismatch.");

        // 1. Violazione perché naturalPersonBeneficiary è richiesto ma assente
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("taxRelief.naturalPersonBeneficiary")),
                "Expected violation on 'taxRelief.naturalPersonBeneficiary' because it's required for NATURAL_PERSON type.");

        // 2. Violazione perché legalPersonBeneficiary non è permesso per NATURAL_PERSON
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("taxRelief.legalPersonBeneficiary")),
                "Expected violation on 'taxRelief.legalPersonBeneficiary' because it's not allowed for NATURAL_PERSON type.");

        assertEquals(2, violations.size(), "Expected exactly 2 violations for the mismatch scenario.");
    }

    @Test
    @DisplayName("Tax relief beneficiary type mismatch (LEGAL_PERSON) should return violations")
    void taxReliefBeneficiaryTypeMismatch_legalPerson_shouldReturnViolations() {
        MoneyTransferRequest request = buildValidRequest();
        TaxReliefRequest taxRelief = new TaxReliefRequest();
        taxRelief.setIsCondoUpgrade(true);
        taxRelief.setCreditorFiscalCode("ABCDEF12G34H567I");
        taxRelief.setBeneficiaryType("LEGAL_PERSON"); // Settiamo LEGAL_PERSON
        taxRelief.setTaxReliefId("DL50");
        // Impostiamo naturalPersonBeneficiary ma NON legalPersonBeneficiary
        taxRelief.setNaturalPersonBeneficiary(new NaturalPersonBeneficiaryRequest("NPFC12345", null, null, null, null));
        request.setTaxRelief(taxRelief);

        Set<ConstraintViolation<MoneyTransferRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Expected violations for tax relief beneficiary type mismatch (LEGAL_PERSON).");

        // 1. Violazione perché legalPersonBeneficiary è richiesto ma assente
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("taxRelief.legalPersonBeneficiary")),
                "Expected violation on 'taxRelief.legalPersonBeneficiary' because it's required for LEGAL_PERSON type.");

        // 2. Violazione perché naturalPersonBeneficiary non è permesso per LEGAL_PERSON
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("taxRelief.naturalPersonBeneficiary")),
                "Expected violation on 'taxRelief.naturalPersonBeneficiary' because it's not allowed for LEGAL_PERSON type.");

        assertEquals(2, violations.size(), "Expected exactly 2 violations for the LEGAL_PERSON mismatch scenario.");
    }
}