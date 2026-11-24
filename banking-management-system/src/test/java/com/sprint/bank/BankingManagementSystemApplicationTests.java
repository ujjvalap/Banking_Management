package com.sprint.bank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.sprint.bank.dto.AuthResponse;
import com.sprint.bank.dto.LoginRequest;
import com.sprint.bank.dto.RegisterRequest;
import com.sprint.bank.entity.Role;
import com.sprint.bank.repository.UserRepository;
import com.sprint.bank.security.JwtService;
import com.sprint.bank.service.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BankingManagementSystemApplicationTests {
	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void clearUsers() {
		userRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void registrationStoresHashedPasswordAndLoginReturnsSignedToken() {
		RegisterRequest request = new RegisterRequest(
				"Alex Morgan",
				"  Alex.Morgan@example.com ",
				"StrongPass123",
				"+1 555 123 4567",
				"12 Market Street",
				"ABCDE1234F");

		AuthResponse registered = authService.register(request);
		var savedUser = userRepository.findByEmail("alex.morgan@example.com").orElseThrow();

		assertEquals(Role.CUSTOMER, savedUser.getRole());
		assertNotEquals(request.password(), savedUser.getPasswordHash());
		assertTrue(passwordEncoder.matches(request.password(), savedUser.getPasswordHash()));
		assertEquals("Alex Morgan", registered.user().fullName());
		assertEquals("alex.morgan@example.com", jwtService.extractSubject(registered.accessToken()));

		AuthResponse loggedIn = authService.login(new LoginRequest("ALEX.MORGAN@example.com", request.password()));
		assertEquals("alex.morgan@example.com", loggedIn.user().email());
	}

	@Test
	void loginRejectsIncorrectPassword() {
		authService.register(new RegisterRequest(
				"Alex Morgan",
				"alex.morgan@example.com",
				"StrongPass123",
				"+1 555 123 4567",
				"12 Market Street",
				"ABCDE1234F"));

		assertThrows(BadCredentialsException.class,
				() -> authService.login(new LoginRequest("alex.morgan@example.com", "WrongPass123")));
	}

	@Test
	void registrationEndpointReturnsTokenAndValidationErrorsUseSharedShape() throws Exception {
		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "fullName": "Taylor Reed",
								  "email": "taylor@example.com",
								  "password": "StrongPass123",
								  "phone": "+1 555 123 4567",
								  "address": "4 River Road",
								  "pan": "FGHIJ5678K"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.user.role").value("CUSTOMER"));

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "fullName": "",
								  "email": "not-an-email",
								  "password": "short",
								  "phone": "",
								  "address": "",
								  "pan": "invalid"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.timestamp").isNotEmpty())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.validationErrors.email").isNotEmpty());

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "fullName": "Taylor Reed",
								  "email": "weak@example.com",
								  "password": "weakpassword123",
								  "phone": "+1 555 123 4567",
								  "address": "4 River Road",
								  "pan": "FGHIJ5678K"
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.validationErrors.password").isNotEmpty());
	}

	@Test
	void currentUserRequiresValidBearerTokenAndAdminRoutesRejectCustomers() throws Exception {
		AuthResponse registration = authService.register(new RegisterRequest(
				"Taylor Reed",
				"taylor@example.com",
				"StrongPass123",
				"+1 555 123 4567",
				"4 River Road",
				"FGHIJ5678K"));

		mockMvc.perform(get("/api/auth/me"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.status").value(401));

		mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + registration.accessToken()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("taylor@example.com"));

		mockMvc.perform(get("/api/admin/customers").header("Authorization", "Bearer " + registration.accessToken()))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.status").value(403));

		mockMvc.perform(get("/unknown-endpoint"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void thymeleafLoginUsesCsrfProtectedServerSession() throws Exception {
		authService.register(new RegisterRequest(
				"Taylor Reed",
				"taylor@example.com",
				"StrongPass123",
				"+1 555 123 4567",
				"4 River Road",
				"FGHIJ5678K"));

		MvcResult loginResult = mockMvc.perform(post("/login")
						.with(csrf())
						.param("username", "TAYLOR@example.com")
						.param("password", "StrongPass123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andReturn();
		MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
		assertNotNull(session);

		mockMvc.perform(get("/").session(session))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("taylor@example.com")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("HttpOnly cookie")));

		mockMvc.perform(post("/logout").session(session).with(csrf()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/?logout"));
	}

	@Test
	void staticAuthenticationAssetsArePubliclyServed() throws Exception {
		mockMvc.perform(get("/css/styles.css"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString(".shell")));

		mockMvc.perform(get("/js/app.js"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("setMode")));
	}

	@Test
	void thymeleafRegistrationCreatesAccountAndRejectsMissingCsrf() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")))
				.andExpect(content().string(org.hamcrest.Matchers.not(
						org.hamcrest.Matchers.containsString("th:action"))));

		mockMvc.perform(post("/register")
						.param("fullName", "Taylor Reed")
						.param("email", "taylor@example.com")
						.param("password", "StrongPass123")
						.param("phone", "+1 555 123 4567")
						.param("address", "4 River Road")
						.param("pan", "FGHIJ5678K"))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/register")
						.with(csrf())
						.param("fullName", "Taylor Reed")
						.param("email", "taylor@example.com")
						.param("password", "StrongPass123")
						.param("phone", "+1 555 123 4567")
						.param("address", "4 River Road")
						.param("pan", "fghij5678k"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/?registered=true"));

		assertTrue(userRepository.existsByEmail("taylor@example.com"));
		assertEquals("FGHIJ5678K",
				userRepository.findByEmail("taylor@example.com").orElseThrow().getProfile().getPan());
	}
}
