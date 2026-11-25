package com.sprint.bank;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.sprint.bank.repository.LoginAttemptRepository;
import com.sprint.bank.repository.PasswordResetTokenRepository;
import com.sprint.bank.repository.RefreshTokenRepository;
import com.sprint.bank.repository.EmailVerificationTokenRepository;
import com.sprint.bank.repository.UserRepository;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BankingManagementSystemApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @BeforeEach
    void clearDatabase() {
        refreshTokenRepository.deleteAll();
        emailVerificationTokenRepository.deleteAll();
        passwordResetTokenRepository.deleteAll();
        loginAttemptRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void registerLoginCurrentUserRefreshAndLogoutWorkEndToEnd() throws Exception {
        String registerBody = """
                {
                  "fullName": "Alex Morgan",
                  "email": "Alex.Morgan@example.com",
                  "password": "StrongPass123!",
                  "phone": "+1 555 123 4567",
                  "address": "12 Market Street",
                  "pan": "ABCDE1234F"
                }
                """;
        MvcResult registration = mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        String accessToken = json(registration, "accessToken");
        String refreshToken = json(registration, "refreshToken");

        mockMvc.perform(get("/api/v1/me").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alex.morgan@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        MvcResult refreshed = mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshToken)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/logout")
                        .header("Authorization", "Bearer " + json(refreshed, "accessToken")))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(json(refreshed, "refreshToken"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void apiValidationDuplicateCredentialsAndAuthenticationAreConsistent() throws Exception {
        mockMvc.perform(get("/api/v1/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"","email":"not-an-email","password":"short","phone":"","address":"","pan":"bad"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").isNotEmpty())
                .andExpect(jsonPath("$.validationErrors.password").isNotEmpty());

        String registrationBody = """
                {
                  "fullName": "Taylor Reed",
                  "email": "taylor@example.com",
                  "password": "StrongPass123!",
                  "phone": "+1 555 123 4567",
                  "address": "4 River Road",
                  "pan": "FGHIJ5678K"
                }
                """;
        MvcResult registrationResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationBody))
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult login = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"TAYLOR@example.com","password":"StrongPass123!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        mockMvc.perform(get("/api/admin/customers")
                        .header("Authorization", "Bearer " + json(login, "accessToken")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"taylor@example.com","password":"WrongPassword123!"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").isNotEmpty());

        mockMvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer " + json(registrationResult, "accessToken")))
                .andExpect(status().isOk());
    }

    @Test
    void browserLoginAndRegistrationRemainCsrfProtected() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"_csrf\"")))
                .andExpect(content().string(not(containsString("th:action"))));

        mockMvc.perform(post("/register")
                        .param("fullName", "Taylor Reed")
                        .param("email", "taylor@example.com")
                        .param("password", "StrongPass123!")
                        .param("phone", "+1 555 123 4567")
                        .param("address", "4 River Road")
                        .param("pan", "FGHIJ5678K"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "Taylor Reed")
                        .param("email", "taylor@example.com")
                        .param("password", "StrongPass123!")
                        .param("phone", "+1 555 123 4567")
                        .param("address", "4 River Road")
                        .param("pan", "FGHIJ5678K"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?registered=true"));

        MvcResult login = mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "TAYLOR@example.com")
                        .param("password", "StrongPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("taylor@example.com")))
                .andExpect(content().string(containsString("HttpOnly cookie")));

        mockMvc.perform(post("/logout").session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?logout=true"));
    }

    @Test
    void staticAuthenticationAssetsAreServed() throws Exception {
        mockMvc.perform(get("/css/styles.css"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(".shell")));

        mockMvc.perform(get("/js/app.js"))
                .andExpect(status().isOk());
    }

    @Test
    void configuredCorsOriginCanMakeApiPreflightRequest() throws Exception {
        mockMvc.perform(options("/api/v1/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void repeatedInvalidLoginsTemporarilyLockTheAccount() throws Exception {
        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fullName":"Jamie Lee",
                                  "email":"jamie@example.com",
                                  "password":"StrongPass123!",
                                  "phone":"+1 555 123 4567",
                                  "address":"8 Lake Street",
                                  "pan":"KLMNO1234P"
                                }
                                """))
                .andExpect(status().isCreated());

        for (int attempt = 0; attempt < 5; attempt++) {
            mockMvc.perform(post("/api/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"jamie@example.com","password":"WrongPassword123!"}
                                    """))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"jamie@example.com","password":"StrongPass123!"}
                                """))
                .andExpect(status().isLocked());
    }

    private String json(MvcResult result, String property) throws Exception {
        return objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get(property)
                .asText();
    }
}
