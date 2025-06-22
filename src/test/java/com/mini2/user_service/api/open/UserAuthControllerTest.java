package com.mini2.user_service.api.open;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 회원가입_성공() throws Exception {
        String requestBody = """
        {
            "email": "test@example.com",
            "password": "Password123!",
            "name": "홍길동"
        }
    """;

        mockMvc.perform(post("/api/user/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"));
    }

    @Test
    void 회원가입_이메일형식_실패() throws Exception {
        String requestBody = """
            {
                "email": "invalid-email",
                "password": "Password123!",
                "name": "홍길동"
            }
        """;

        mockMvc.perform(post("/api/user/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ParameterNotValid"))
                .andExpect(jsonPath("$.data.errors[0].field").value("email"));
    }
}