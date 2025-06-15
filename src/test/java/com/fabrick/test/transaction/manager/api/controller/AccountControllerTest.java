package com.fabrick.test.transaction.manager.api.controller;

// AccountControllerIT.java

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountRequestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getBalance_invalidAccountId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/abc/balance")
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.description").value(org.hamcrest.Matchers.containsString("Account ID must contain only digits")));
    }

    @Test
    void getBalance_emptyAccountId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/accounts//balance")
                        .header("Authorization", "Bearer test"))
                .andExpect(status().isNotFound());
    }
}