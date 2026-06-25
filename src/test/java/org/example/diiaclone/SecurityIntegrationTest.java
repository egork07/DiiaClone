package org.example.diiaclone;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.diiaclone.dto.auth.LoginRequest;
import org.example.diiaclone.dto.auth.RegisterRequest;
import org.example.diiaclone.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AppUserRepository userRepository;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        adminToken = registerAndGetToken("admin", "adminpass");
        userToken  = registerAndGetToken("user",  "userpass");
    }

    @Test
    @DisplayName("Регистрация возвращает JWT токен")
    void register_returnsToken() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setPassword("password123");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("newuser"));
    }

    @Test
    @DisplayName("Логин с неверным паролем — 401")
    void login_wrongPassword_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("wrongpassword");

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Запрос без токена — 401")
    void noToken_returns401() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/users"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("USER может читать список пользователей")
    void user_canGetUsers() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/users")
                                .header("Authorization", "Bearer " + userToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("USER не может создать пользователя — 403")
    void user_cannotCreateUser_returns403() throws Exception {
        String body = """
                {"fullName": "Test User", "email": "test@test.com"}
                """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/users")
                                .header("Authorization", "Bearer " + userToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("USER не может удалить пользователя — 403")
    void user_cannotDeleteUser_returns403() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/users/1")
                                .header("Authorization", "Bearer " + userToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN может создать пользователя — 201")
    void admin_canCreateUser() throws Exception {
        String body = """
                {"fullName": "Admin Created", "email": "admin@test.com"}
                """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/users")
                                .header("Authorization", "Bearer " + adminToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Невалидный JWT — 401")
    void invalidJwt_returns401() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/users")
                                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // ── Вспомогательный метод ─────────────────────────────────────────────

    private String registerAndGetToken(String username, String password)
            throws Exception {

        RegisterRequest req = new RegisterRequest();
        req.setUsername(username);
        req.setPassword(password);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}