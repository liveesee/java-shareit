package ru.practicum.shareit.gateway.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.user.dto.UserDto;

class UserClientTest {
	private UserClient userClient;
	private MockRestServiceServer server;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		userClient = new UserClient(new RestTemplateBuilder(), objectMapper, "http://localhost:9090");
		RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(userClient, "restTemplate");
		server = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	void createShouldPostToUsers() throws Exception {
		UserDto dto = UserDto.builder().name("Ivan").email("ivan@test.com").build();
		server.expect(requestTo("http://localhost:9090/users"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(content().json(objectMapper.writeValueAsString(dto)))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertEquals(200, userClient.create(dto).getStatusCode().value());
	}

	@Test
	void updateShouldPatchUser() {
		server.expect(requestTo("http://localhost:9090/users/1"))
				.andExpect(method(HttpMethod.PATCH))
				.andRespond(withSuccess());

		assertEquals(200, userClient.update(1L, UserDto.builder().name("New").build()).getStatusCode().value());
	}

	@Test
	void getByIdShouldCallUsersPath() {
		server.expect(requestTo("http://localhost:9090/users/1"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, userClient.getById(1L).getStatusCode().value());
	}

	@Test
	void getAllShouldCallUsersPath() {
		server.expect(requestTo("http://localhost:9090/users"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess());

		assertEquals(200, userClient.getAll().getStatusCode().value());
	}

	@Test
	void deleteShouldCallUsersPath() {
		server.expect(requestTo("http://localhost:9090/users/1"))
				.andExpect(method(HttpMethod.DELETE))
				.andRespond(withSuccess());

		assertEquals(200, userClient.delete(1L).getStatusCode().value());
	}
}
