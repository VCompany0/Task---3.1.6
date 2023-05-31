package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WithMockUser(authorities = "ROLE_MODERATOR")
class RestExceptionHandlerTest extends BaseIntegrationTest {

    @Test
    void handleException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + testId)).andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void handleInvalidArgument() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .content("""
                        {}
                        """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}